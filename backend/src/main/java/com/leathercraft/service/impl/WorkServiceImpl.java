package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.common.WorkImageType;
import com.leathercraft.dto.CraftProfileDTO;
import com.leathercraft.dto.WorkPublishDTO;
import com.leathercraft.dto.WorkStepDTO;
import com.leathercraft.entity.Work;
import com.leathercraft.entity.WorkImage;
import com.leathercraft.entity.WorkStep;
import com.leathercraft.mapper.WorkImageMapper;
import com.leathercraft.mapper.WorkMapper;
import com.leathercraft.mapper.WorkStepMapper;
import com.leathercraft.service.WorkService;
import com.leathercraft.validator.MaterialValidator;
import com.leathercraft.validator.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work> implements WorkService {

    @Autowired
    private WorkImageMapper workImageMapper;

    @Autowired
    private WorkStepMapper workStepMapper;

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MaterialValidator materialValidator;

    private static final String HOT_WORKS_KEY = "hot:works";
    private static final String WORK_VIEW_KEY = "work:view:";
    private static final int MATERIAL_BRIEF_MAX_LENGTH = 100;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[][] CRAFT_KEYWORDS = {
        {"封边", "磨边", "边油", "床面", "封边上色"},
        {"打孔", "打斩", "冲孔", "斩孔", "菱斩"},
        {"缝线", "缝制", "手缝", "马鞍缝", "双针", "线迹"},
        {"上色", "染色", "涂色", "皮雕上色"},
        {"皮雕", "雕刻", "雕花", "唐草"},
        {"塑形", "起鼓", "塑型", "定型"},
        {"五金安装", "五金", "四合扣", "气眼", "铆钉", "拉链", "扣"},
        {"裁切", "裁皮", "下料", "切割", "裁断"},
        {"编织", "编", "织"},
        {"削薄", "削边", "薄削", "铲薄"}
    };

    private static final String[] CRAFT_NAMES = {
        "封边", "打孔", "缝线", "上色", "皮雕", "塑形", "五金安装", "裁切", "编织", "削薄"
    };

    @Override
    public IPage<Work> getWorkPage(Integer page, Integer size, Long categoryId, Long craftTypeId, Long userId) {
        Page<Work> pageParam = buildPage(page, size);
        IPage<Work> result = baseMapper.selectWorkPage(pageParam, categoryId, craftTypeId);
        markFinishedIfOverflow(result);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), WorkImageType.FINISHED));
            if (userId != null) {
                work.setIsFavorite(checkFavorite(userId, work.getId()));
            }
        });
        return result;
    }

    private Page<Work> buildPage(Integer page, Integer size) {
        long current = page == null || page < 1 ? 1 : page;
        long pageSize = size == null || size < 1 ? 10 : Math.min(size, 50);
        return new Page<>(current, pageSize);
    }

    private void markFinishedIfOverflow(IPage<Work> result) {
        if (result.getRecords().isEmpty()
                && result.getTotal() > 0
                && result.getPages() > 0
                && result.getCurrent() > result.getPages()) {
            result.setTotal(0);
        }
    }

    @Override
    public Work getWorkDetail(Long id, Long userId) {
        Work work = baseMapper.selectWorkDetail(id);
        if (work == null && userId != null) {
            Work anyStatusWork = baseMapper.selectWorkDetailAnyStatus(id);
            if (anyStatusWork != null && anyStatusWork.getUserId().equals(userId)) {
                work = anyStatusWork;
            }
        }
        if (work != null) {
            work.setImages(workImageMapper.selectImagesByWorkId(id, WorkImageType.FINISHED));
            work.setProcessImages(workImageMapper.selectImagesByWorkId(id, WorkImageType.PROCESS));
            work.setDetailImages(workImageMapper.selectImagesByWorkId(id, WorkImageType.DETAIL));
            loadWorkSteps(work);
            extractCraftHighlights(work);
            if (userId != null) {
                work.setIsFavorite(checkFavorite(userId, id));
            }
        }
        return work;
    }

    private void loadWorkSteps(Work work) {
        List<WorkStep> steps = workStepMapper.selectStepsByWorkId(work.getId());
        if (steps != null && !steps.isEmpty()) {
            steps.forEach(step -> {
                step.setImages(workImageMapper.selectImagesByStepId(step.getId()));
            });
            work.setSteps(steps);
        } else {
            work.setSteps(parseCraftStepsFromText(work));
        }
    }

    private List<WorkStep> parseCraftStepsFromText(Work work) {
        List<WorkStep> steps = new ArrayList<>();
        String craftSteps = work.getCraftSteps();
        if (craftSteps == null || craftSteps.trim().isEmpty()) {
            return steps;
        }

        Pattern pattern = Pattern.compile("(\\d+)\\.\\s*([^\n]+)");
        Matcher matcher = pattern.matcher(craftSteps);
        int sort = 0;
        List<String> processImages = work.getProcessImages();

        while (matcher.find()) {
            WorkStep step = new WorkStep();
            String stepName = matcher.group(2).trim();
            step.setStepName(stepName);
            step.setStepType(detectStepType(stepName));
            step.setSort(sort);

            if (processImages != null && sort < processImages.size()) {
                List<String> stepImages = new ArrayList<>();
                stepImages.add(processImages.get(sort));
                step.setImages(stepImages);
            }

            steps.add(step);
            sort++;
        }

        if (steps.isEmpty()) {
            String[] lines = craftSteps.split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.isEmpty()) {
                    WorkStep step = new WorkStep();
                    String cleanName = line.replaceAll("^\\d+[.、)\\s]+", "").trim();
                    step.setStepName(cleanName.isEmpty() ? line : cleanName);
                    step.setStepType(detectStepType(step.getStepName()));
                    step.setSort(i);

                    if (processImages != null && i < processImages.size()) {
                        List<String> stepImages = new ArrayList<>();
                        stepImages.add(processImages.get(i));
                        step.setImages(stepImages);
                    }

                    steps.add(step);
                }
            }
        }

        return steps;
    }

    private String detectStepType(String stepName) {
        if (stepName == null) return "other";
        String lower = stepName.toLowerCase();
        if (lower.contains("裁") || lower.contains("切") || lower.contains("下料") || lower.contains("cut")) {
            return "cutting";
        }
        if (lower.contains("缝") || lower.contains("缝制") || lower.contains("sew") || lower.contains("stitch")) {
            return "sewing";
        }
        if (lower.contains("封边") || lower.contains("磨边") || lower.contains("edge") || lower.contains("边油") || lower.contains("上色")) {
            return "edge";
        }
        if (lower.contains("五金") || lower.contains("安装") || lower.contains("扣") || lower.contains("hardware")) {
            return "hardware";
        }
        if (lower.contains("塑形") || lower.contains("起鼓") || lower.contains("shape")) {
            return "shaping";
        }
        if (lower.contains("皮雕") || lower.contains("雕刻") || lower.contains("carve")) {
            return "carving";
        }
        return "other";
    }

    private String generateMaterialBrief(String materialSummary, String materialsText) {
        StringBuilder brief = new StringBuilder();

        if (materialSummary != null && !materialSummary.trim().isEmpty()) {
            try {
                JsonNode root = objectMapper.readTree(materialSummary);
                List<String> parts = new ArrayList<>();

                appendMaterialItems(parts, root.path("mainMaterials"), "主材");
                appendMaterialItems(parts, root.path("auxMaterials"), "辅材");
                appendMaterialItems(parts, root.path("tools"), "工具");

                brief.append(String.join("；", parts));
            } catch (Exception e) {
                // JSON 解析失败，回退到文本模式
            }
        }

        if (brief.length() == 0 && materialsText != null && !materialsText.trim().isEmpty()) {
            String clean = materialsText.trim().replaceAll("\\s+", " ");
            brief.append(clean);
        }

        String result = brief.toString();
        if (result.length() > MATERIAL_BRIEF_MAX_LENGTH) {
            result = result.substring(0, MATERIAL_BRIEF_MAX_LENGTH - 1) + "…";
        }
        return result;
    }

    private void appendMaterialItems(List<String> parts, JsonNode items, String label) {
        if (items == null || !items.isArray() || items.size() == 0) {
            return;
        }
        List<String> names = new ArrayList<>();
        for (JsonNode item : items) {
            StringBuilder sb = new StringBuilder();
            String name = item.path("name").asText("");
            if (!name.isEmpty()) {
                sb.append(name);
            }
            String spec = item.path("spec").asText("");
            if (!spec.isEmpty()) {
                sb.append(spec);
            }
            if (sb.length() > 0) {
                names.add(sb.toString());
            }
        }
        if (!names.isEmpty()) {
            parts.add(label + "：" + String.join("、", names));
        }
    }

    private void extractCraftHighlights(Work work) {
        List<String> highlights = new ArrayList<>();
        boolean[] found = new boolean[CRAFT_NAMES.length];

        StringBuilder allText = new StringBuilder();
        if (work.getTitle() != null) {
            allText.append(work.getTitle()).append(" ");
        }
        if (work.getContent() != null) {
            allText.append(work.getContent()).append(" ");
        }
        if (work.getCraftSteps() != null) {
            allText.append(work.getCraftSteps()).append(" ");
        }
        if (work.getMaterials() != null) {
            allText.append(work.getMaterials()).append(" ");
        }

        List<WorkStep> steps = work.getSteps();
        if (steps != null) {
            for (WorkStep step : steps) {
                if (step.getStepName() != null) {
                    allText.append(step.getStepName()).append(" ");
                }
                if (step.getDescription() != null) {
                    allText.append(step.getDescription()).append(" ");
                }
                if (step.getTips() != null) {
                    allText.append(step.getTips()).append(" ");
                }
                if (step.getMaterials() != null) {
                    allText.append(step.getMaterials()).append(" ");
                }
            }
        }

        String text = allText.toString();
        for (int i = 0; i < CRAFT_KEYWORDS.length; i++) {
            for (String keyword : CRAFT_KEYWORDS[i]) {
                if (text.contains(keyword)) {
                    found[i] = true;
                    break;
                }
            }
        }

        for (int i = 0; i < CRAFT_NAMES.length; i++) {
            if (found[i]) {
                highlights.add(CRAFT_NAMES[i]);
            }
        }

        work.setCraftHighlights(highlights);
    }

    @Override
    @Transactional
    public void publishWork(WorkPublishDTO dto, Long userId) {
        validateMaterials(dto.getMaterialSummary(), dto.getMaterials());

        Work work = new Work();
        work.setUserId(userId);
        work.setTitle(dto.getTitle());
        syncCoverFromImages(work, dto.getCover(), dto.getImages());
        work.setContent(dto.getContent());
        work.setMaterials(dto.getMaterials());
        work.setMaterialSummary(dto.getMaterialSummary());
        work.setMaterialBrief(generateMaterialBrief(dto.getMaterialSummary(), dto.getMaterials()));
        work.setCraftSteps(dto.getCraftSteps());
        work.setCategoryId(dto.getCategoryId());
        work.setCraftTypeId(dto.getCraftTypeId());
        work.setViewCount(0);
        work.setFavoriteCount(0);
        work.setStatus(1);
        work.setCreateTime(LocalDateTime.now());
        work.setUpdateTime(LocalDateTime.now());
        save(work);

        saveWorkImages(work.getId(), null, dto.getImages(), WorkImageType.FINISHED);
        saveWorkImages(work.getId(), null, dto.getProcessImages(), WorkImageType.PROCESS);
        saveWorkImages(work.getId(), null, dto.getDetailImages(), WorkImageType.DETAIL);
        saveWorkSteps(work.getId(), dto.getSteps());
    }

    @Override
    @Transactional
    public void updateWork(WorkPublishDTO dto, Long userId) {
        validateMaterials(dto.getMaterialSummary(), dto.getMaterials());

        Work work = getById(dto.getId());
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }
        work.setTitle(dto.getTitle());
        syncCoverFromImages(work, dto.getCover(), dto.getImages());
        work.setContent(dto.getContent());
        work.setMaterials(dto.getMaterials());
        work.setMaterialSummary(dto.getMaterialSummary());
        work.setMaterialBrief(generateMaterialBrief(dto.getMaterialSummary(), dto.getMaterials()));
        work.setCraftSteps(dto.getCraftSteps());
        work.setCategoryId(dto.getCategoryId());
        work.setCraftTypeId(dto.getCraftTypeId());
        work.setUpdateTime(LocalDateTime.now());
        updateById(work);

        cleanupWorkImages(dto.getId());

        saveWorkImages(work.getId(), null, dto.getImages(), WorkImageType.FINISHED);
        saveWorkImages(work.getId(), null, dto.getProcessImages(), WorkImageType.PROCESS);
        saveWorkImages(work.getId(), null, dto.getDetailImages(), WorkImageType.DETAIL);
        saveWorkSteps(work.getId(), dto.getSteps());
    }

    @Override
    @Transactional
    public void offlineWork(Long id, Long userId) {
        Work work = getById(id);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        work.setStatus(0);
        work.setUpdateTime(LocalDateTime.now());
        updateById(work);
        cleanupWorkImages(id);
    }

    @Override
    public IPage<Work> getUserWorks(Integer page, Integer size, Long userId) {
        Page<Work> pageParam = buildPage(page, size);
        IPage<Work> result = baseMapper.selectUserWorks(pageParam, userId);
        markFinishedIfOverflow(result);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), WorkImageType.FINISHED));
        });
        return result;
    }

    @Override
    public IPage<Work> getFavoriteWorks(Integer page, Integer size, Long userId) {
        Page<Work> pageParam = buildPage(page, size);
        IPage<Work> result = baseMapper.selectFavoriteWorks(pageParam, userId);
        markFinishedIfOverflow(result);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), WorkImageType.FINISHED));
            work.setIsFavorite(true);
        });
        return result;
    }

    @Override
    public List<Work> getHotWorks() {
        List<Work> hotWorks = baseMapper.selectHotWorks(10);
        hotWorks.forEach(work -> work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), WorkImageType.FINISHED)));
        return hotWorks;
    }

    @Override
    public void incrementViewCount(Long id) {
        String key = WORK_VIEW_KEY + id;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);

        Work work = getById(id);
        if (work != null) {
            work.setViewCount(work.getViewCount() + 1);
            updateById(work);
        }
    }

    private void saveWorkImages(Long workId, Long stepId, List<String> images, Integer type) {
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                WorkImage workImage = new WorkImage();
                workImage.setWorkId(workId);
                workImage.setStepId(stepId);
                workImage.setImageUrl(images.get(i));
                workImage.setType(type);
                workImage.setSort(i);
                workImage.setCreateTime(LocalDateTime.now());
                workImageMapper.insert(workImage);
            }
        }
    }

    private void saveWorkSteps(Long workId, List<WorkStepDTO> stepDTOs) {
        if (stepDTOs != null && !stepDTOs.isEmpty()) {
            for (int i = 0; i < stepDTOs.size(); i++) {
                WorkStepDTO dto = stepDTOs.get(i);
                WorkStep step = new WorkStep();
                step.setWorkId(workId);
                step.setStepName(dto.getStepName());
                step.setStepType(dto.getStepType());
                step.setTimeCost(dto.getTimeCost());
                step.setMaterials(dto.getMaterials());
                step.setTips(dto.getTips());
                step.setDescription(dto.getDescription());
                step.setSort(dto.getSort() != null ? dto.getSort() : i);
                step.setCreateTime(LocalDateTime.now());
                workStepMapper.insert(step);

                if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                    saveWorkImages(workId, step.getId(), dto.getImages(), WorkImageType.PROCESS);
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteWork(Long id, Long userId) {
        Work work = getById(id);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        cleanupWorkImages(id);
        removeById(id);
    }

    private void cleanupWorkImages(Long workId) {
        workImageMapper.deleteStepImagesByWorkId(workId);
        workImageMapper.deleteByWorkId(workId);
        workStepMapper.deleteByWorkId(workId);
    }

    private void syncCoverFromImages(Work work, String rawCover, List<String> images) {
        if (images != null && !images.isEmpty()) {
            String firstImage = images.get(0);
            if (rawCover != null && !rawCover.isEmpty()) {
                boolean coverInImages = false;
                for (String img : images) {
                    if (img != null && img.equals(rawCover)) {
                        coverInImages = true;
                        break;
                    }
                }
                work.setCover(coverInImages ? rawCover : firstImage);
            } else {
                work.setCover(firstImage);
            }
        } else {
            work.setCover(rawCover);
        }
    }

    private boolean checkFavorite(Long userId, Long workId) {
        return favoriteService.isFavorite(userId, workId);
    }

    private void validateMaterials(String materialSummary, String materialsText) {
        ValidationResult result = new ValidationResult();
        result.merge(materialValidator.validateMaterialSummary(materialSummary));
        result.merge(materialValidator.validateMaterialsText(materialsText));

        if (!result.isValid()) {
            throw new RuntimeException("材料信息校验失败：" + result.getErrorSummary());
        }
    }

    public ValidationResult validateMaterialsFull(String materialSummary, String materialsText) {
        ValidationResult result = new ValidationResult();
        result.merge(materialValidator.validateMaterialSummary(materialSummary));
        result.merge(materialValidator.validateMaterialsText(materialsText));
        return result;
    }

    @Override
    public CraftProfileDTO getUserCraftProfile(Long userId) {
        CraftProfileDTO profile = new CraftProfileDTO();

        List<Map<String, Object>> topCatRows = baseMapper.selectTopCategories(userId);
        List<CraftProfileDTO.CategoryStat> topCategories = new ArrayList<>();
        for (Map<String, Object> row : topCatRows) {
            Long id = row.get("id") != null ? ((Number) row.get("id")).longValue() : null;
            String name = (String) row.get("name");
            Integer count = row.get("count") != null ? ((Number) row.get("count")).intValue() : 0;
            topCategories.add(new CraftProfileDTO.CategoryStat(id, name, count));
        }
        profile.setTopCategories(topCategories);

        List<Map<String, Object>> topCraftRows = baseMapper.selectTopCraftTypes(userId);
        List<CraftProfileDTO.CategoryStat> topCraftTypes = new ArrayList<>();
        for (Map<String, Object> row : topCraftRows) {
            Long id = row.get("id") != null ? ((Number) row.get("id")).longValue() : null;
            String name = (String) row.get("name");
            Integer count = row.get("count") != null ? ((Number) row.get("count")).intValue() : 0;
            topCraftTypes.add(new CraftProfileDTO.CategoryStat(id, name, count));
        }
        profile.setTopCraftTypes(topCraftTypes);

        Integer recentCount = baseMapper.selectRecentCount(userId);
        profile.setRecentCompleted(recentCount != null ? recentCount : 0);

        Integer totalCount = baseMapper.selectTotalCount(userId);
        profile.setTotalWorks(totalCount != null ? totalCount : 0);

        List<String> commonMaterials = extractCommonMaterials(userId);
        profile.setCommonMaterials(commonMaterials);

        return profile;
    }

    private List<String> extractCommonMaterials(Long userId) {
        List<Map<String, Object>> materialRows = baseMapper.selectMaterialsForProfile(userId);
        Map<String, Integer> materialCount = new LinkedHashMap<>();

        for (Map<String, Object> row : materialRows) {
            String materials = (String) row.get("materials");
            String materialSummary = (String) row.get("material_summary");

            if (materialSummary != null && !materialSummary.trim().isEmpty()) {
                try {
                    JsonNode root = objectMapper.readTree(materialSummary);
                    collectMaterialNames(root.path("mainMaterials"), materialCount);
                    collectMaterialNames(root.path("auxMaterials"), materialCount);
                } catch (Exception ignored) {}
            }

            if (materials != null && !materials.trim().isEmpty()) {
                String[] parts = materials.split("[,，、；;\\s]+");
                for (String part : parts) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty() && trimmed.length() <= 20) {
                        materialCount.merge(trimmed, 1, Integer::sum);
                    }
                }
            }
        }

        return materialCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    private void collectMaterialNames(JsonNode items, Map<String, Integer> materialCount) {
        if (items == null || !items.isArray()) {
            return;
        }
        for (JsonNode item : items) {
            String name = item.path("name").asText("");
            if (!name.isEmpty() && name.length() <= 20) {
                materialCount.merge(name, 1, Integer::sum);
            }
        }
    }
}
