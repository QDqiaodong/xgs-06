package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.dto.WorkPublishDTO;
import com.leathercraft.dto.WorkStepDTO;
import com.leathercraft.entity.Work;
import com.leathercraft.entity.WorkImage;
import com.leathercraft.entity.WorkStep;
import com.leathercraft.mapper.WorkImageMapper;
import com.leathercraft.mapper.WorkMapper;
import com.leathercraft.mapper.WorkStepMapper;
import com.leathercraft.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private static final String HOT_WORKS_KEY = "hot:works";
    private static final String WORK_VIEW_KEY = "work:view:";

    @Override
    public IPage<Work> getWorkPage(Integer page, Integer size, Long categoryId, Long craftTypeId, Long userId) {
        Page<Work> pageParam = buildPage(page, size);
        IPage<Work> result = baseMapper.selectWorkPage(pageParam, categoryId, craftTypeId);
        markFinishedIfOverflow(result);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1));
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
        if (work != null) {
            work.setImages(workImageMapper.selectImagesByWorkId(id, 1));
            work.setProcessImages(workImageMapper.selectImagesByWorkId(id, 2));
            loadWorkSteps(work);
            work.setCraftHighlights(extractCraftHighlights(work));
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

    private static final List<String[]> CRAFT_KEYWORDS = Arrays.asList(
        new String[]{"封边", "磨边", "边油", "抛光", "床面处理", "封边上色", "边缘处理"},
        new String[]{"打孔", "打斩", "冲眼", "冲孔", "菱斩", "斩孔"},
        new String[]{"缝线", "缝制", "双针缝", "马鞍缝", "手缝", "线迹", "麻线"},
        new String[]{"上色", "染色", "涂色", "边油", "酒染", "丙烯"},
        new String[]{"皮雕", "雕刻", "唐草", "植鞣雕", "印花", "压花"},
        new String[]{"塑形", "起鼓", "定型", "塑型", "填充", "模具"},
        new String[]{"五金安装", "四合扣", "气眼", "拉链", "铆钉", "按扣", "磁扣"},
        new String[]{"裁切", "下料", "裁皮", "切割", "裁料"},
        new String[]{"编织", "编织工艺", "皮编"},
        new String[]{"削薄", "铲薄", "削边", "片料"}
    );

    private static final List<String> CRAFT_STANDARD_NAMES = Arrays.asList(
        "封边", "打孔", "缝线", "上色", "皮雕", "塑形", "五金安装", "裁切", "编织", "削薄"
    );

    private List<String> extractCraftHighlights(Work work) {
        Set<String> highlights = new LinkedHashSet<>();
        StringBuilder allText = new StringBuilder();

        if (work.getContent() != null) {
            allText.append(work.getContent()).append(" ");
        }
        if (work.getMaterials() != null) {
            allText.append(work.getMaterials()).append(" ");
        }
        if (work.getCraftSteps() != null) {
            allText.append(work.getCraftSteps()).append(" ");
        }
        if (work.getSteps() != null) {
            for (WorkStep step : work.getSteps()) {
                if (step.getStepName() != null) {
                    allText.append(step.getStepName()).append(" ");
                }
                if (step.getDescription() != null) {
                    allText.append(step.getDescription()).append(" ");
                }
                if (step.getTips() != null) {
                    allText.append(step.getTips()).append(" ");
                }
                if (step.getStepType() != null) {
                    allText.append(mapStepTypeToName(step.getStepType())).append(" ");
                }
            }
        }

        String text = allText.toString().toLowerCase();

        for (int i = 0; i < CRAFT_KEYWORDS.size(); i++) {
            String[] keywords = CRAFT_KEYWORDS.get(i);
            for (String keyword : keywords) {
                if (text.contains(keyword.toLowerCase())) {
                    highlights.add(CRAFT_STANDARD_NAMES.get(i));
                    break;
                }
            }
        }

        return new ArrayList<>(highlights);
    }

    private String mapStepTypeToName(String stepType) {
        switch (stepType) {
            case "cutting": return "裁切";
            case "sewing": return "缝线";
            case "edge": return "封边";
            case "hardware": return "五金安装";
            case "shaping": return "塑形";
            case "carving": return "皮雕";
            default: return "";
        }
    }

    @Override
    @Transactional
    public void publishWork(WorkPublishDTO dto, Long userId) {
        Work work = new Work();
        work.setUserId(userId);
        work.setTitle(dto.getTitle());
        work.setCover(dto.getCover());
        work.setContent(dto.getContent());
        work.setMaterials(dto.getMaterials());
        work.setMaterialSummary(dto.getMaterialSummary());
        work.setCraftSteps(dto.getCraftSteps());
        work.setCategoryId(dto.getCategoryId());
        work.setCraftTypeId(dto.getCraftTypeId());
        work.setViewCount(0);
        work.setFavoriteCount(0);
        work.setStatus(1);
        work.setCreateTime(LocalDateTime.now());
        work.setUpdateTime(LocalDateTime.now());
        save(work);

        saveWorkImages(work.getId(), null, dto.getImages(), 1);
        saveWorkImages(work.getId(), null, dto.getProcessImages(), 2);
        saveWorkSteps(work.getId(), dto.getSteps());
    }

    @Override
    @Transactional
    public void updateWork(WorkPublishDTO dto, Long userId) {
        Work work = getById(dto.getId());
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }
        work.setTitle(dto.getTitle());
        work.setCover(dto.getCover());
        work.setContent(dto.getContent());
        work.setMaterials(dto.getMaterials());
        work.setMaterialSummary(dto.getMaterialSummary());
        work.setCraftSteps(dto.getCraftSteps());
        work.setCategoryId(dto.getCategoryId());
        work.setCraftTypeId(dto.getCraftTypeId());
        work.setUpdateTime(LocalDateTime.now());
        updateById(work);

        workImageMapper.delete(new LambdaQueryWrapper<WorkImage>()
                .eq(WorkImage::getWorkId, dto.getId()));
        workStepMapper.delete(new LambdaQueryWrapper<WorkStep>()
                .eq(WorkStep::getWorkId, dto.getId()));

        saveWorkImages(work.getId(), null, dto.getImages(), 1);
        saveWorkImages(work.getId(), null, dto.getProcessImages(), 2);
        saveWorkSteps(work.getId(), dto.getSteps());
    }

    @Override
    public void offlineWork(Long id, Long userId) {
        Work work = getById(id);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        work.setStatus(0);
        work.setUpdateTime(LocalDateTime.now());
        updateById(work);
    }

    @Override
    public IPage<Work> getUserWorks(Integer page, Integer size, Long userId) {
        Page<Work> pageParam = buildPage(page, size);
        IPage<Work> result = baseMapper.selectUserWorks(pageParam, userId);
        markFinishedIfOverflow(result);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1));
        });
        return result;
    }

    @Override
    public IPage<Work> getFavoriteWorks(Integer page, Integer size, Long userId) {
        Page<Work> pageParam = buildPage(page, size);
        IPage<Work> result = baseMapper.selectFavoriteWorks(pageParam, userId);
        markFinishedIfOverflow(result);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1));
            work.setIsFavorite(true);
        });
        return result;
    }

    @Override
    public List<Work> getHotWorks() {
        List<Work> hotWorks = baseMapper.selectHotWorks(10);
        hotWorks.forEach(work -> work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1)));
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
                step.setMaterials(dto.getMaterials());
                step.setTips(dto.getTips());
                step.setDescription(dto.getDescription());
                step.setSort(dto.getSort() != null ? dto.getSort() : i);
                step.setCreateTime(LocalDateTime.now());
                workStepMapper.insert(step);

                if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                    saveWorkImages(workId, step.getId(), dto.getImages(), 2);
                }
            }
        }
    }

    private boolean checkFavorite(Long userId, Long workId) {
        return favoriteService.isFavorite(userId, workId);
    }
}
