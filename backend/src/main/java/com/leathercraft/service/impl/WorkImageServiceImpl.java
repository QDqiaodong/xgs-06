package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.leathercraft.common.WorkImageType;
import com.leathercraft.dto.WorkImageArrangeDTO;
import com.leathercraft.dto.WorkImageMoveDTO;
import com.leathercraft.entity.Work;
import com.leathercraft.entity.WorkImage;
import com.leathercraft.mapper.WorkImageMapper;
import com.leathercraft.mapper.WorkMapper;
import com.leathercraft.service.WorkImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkImageServiceImpl implements WorkImageService {

    @Autowired
    private WorkImageMapper workImageMapper;

    @Autowired
    private WorkMapper workMapper;

    @Override
    public Map<Integer, List<WorkImage>> getImagesGroupedByType(Long workId) {
        List<WorkImage> allImages = workImageMapper.selectList(
                new LambdaQueryWrapper<WorkImage>()
                        .eq(WorkImage::getWorkId, workId)
                        .isNull(WorkImage::getStepId)
                        .orderByAsc(WorkImage::getType, WorkImage::getSort)
        );

        Map<Integer, List<WorkImage>> grouped = new HashMap<>();
        grouped.put(WorkImageType.FINISHED, new ArrayList<>());
        grouped.put(WorkImageType.PROCESS, new ArrayList<>());
        grouped.put(WorkImageType.DETAIL, new ArrayList<>());

        for (WorkImage image : allImages) {
            grouped.computeIfAbsent(image.getType(), k -> new ArrayList<>()).add(image);
        }

        return grouped;
    }

    @Override
    @Transactional
    public void arrangeImages(WorkImageArrangeDTO dto, Long userId) {
        Work work = workMapper.selectById(dto.getWorkId());
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        cleanupWorkLevelImages(dto.getWorkId());

        saveImageGroup(dto.getWorkId(), dto.getFinishedImages(), WorkImageType.FINISHED);
        saveImageGroup(dto.getWorkId(), dto.getProcessImages(), WorkImageType.PROCESS);
        saveImageGroup(dto.getWorkId(), dto.getDetailImages(), WorkImageType.DETAIL);
    }

    @Override
    @Transactional
    public void moveImage(WorkImageMoveDTO dto, Long userId) {
        Work work = workMapper.selectById(dto.getWorkId());
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        if (!WorkImageType.isValid(dto.getTargetType())) {
            throw new RuntimeException("无效的图片类型");
        }

        WorkImage image = workImageMapper.selectById(dto.getImageId());
        if (image == null || !image.getWorkId().equals(dto.getWorkId())) {
            throw new RuntimeException("图片不存在或不属于该作品");
        }

        if (image.getStepId() != null) {
            throw new RuntimeException("不能移动步骤关联的图片");
        }

        int oldType = image.getType();
        int oldSort = image.getSort();
        int newType = dto.getTargetType();
        int newSort = dto.getTargetSort() != null ? dto.getTargetSort() : 0;

        if (oldType == newType && oldSort == newSort) {
            return;
        }

        adjustSortForRemoval(oldType, oldSort, dto.getWorkId());

        adjustSortForInsertion(newType, newSort, dto.getWorkId());

        image.setType(newType);
        image.setSort(newSort);
        workImageMapper.updateById(image);
    }

    @Override
    @Transactional
    public void updateImageSort(Long workId, Long imageId, Integer sort, Long userId) {
        Work work = workMapper.selectById(workId);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        WorkImage image = workImageMapper.selectById(imageId);
        if (image == null || !image.getWorkId().equals(workId)) {
            throw new RuntimeException("图片不存在或不属于该作品");
        }

        if (image.getStepId() != null) {
            throw new RuntimeException("不能调整步骤关联图片的顺序");
        }

        if (sort == null || sort < 0) {
            throw new RuntimeException("无效的排序值");
        }

        int oldSort = image.getSort();
        int newSort = sort;
        int type = image.getType();

        if (oldSort == newSort) {
            return;
        }

        if (newSort > oldSort) {
            workImageMapper.update(null,
                    new LambdaUpdateWrapper<WorkImage>()
                            .eq(WorkImage::getWorkId, workId)
                            .eq(WorkImage::getType, type)
                            .isNull(WorkImage::getStepId)
                            .gt(WorkImage::getSort, oldSort)
                            .le(WorkImage::getSort, newSort)
                            .setSql("sort = sort - 1")
            );
        } else {
            workImageMapper.update(null,
                    new LambdaUpdateWrapper<WorkImage>()
                            .eq(WorkImage::getWorkId, workId)
                            .eq(WorkImage::getType, type)
                            .isNull(WorkImage::getStepId)
                            .lt(WorkImage::getSort, oldSort)
                            .ge(WorkImage::getSort, newSort)
                            .setSql("sort = sort + 1")
            );
        }

        image.setSort(newSort);
        workImageMapper.updateById(image);
    }

    @Override
    @Transactional
    public void addImage(Long workId, String imageUrl, Integer type, Integer sort, Long userId) {
        Work work = workMapper.selectById(workId);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        if (!WorkImageType.isValid(type)) {
            throw new RuntimeException("无效的图片类型");
        }

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new RuntimeException("图片URL不能为空");
        }

        int maxSort = getMaxSort(workId, type);
        int targetSort = sort != null && sort >= 0 ? Math.min(sort, maxSort + 1) : maxSort + 1;

        adjustSortForInsertion(type, targetSort, workId);

        WorkImage newImage = new WorkImage();
        newImage.setWorkId(workId);
        newImage.setStepId(null);
        newImage.setImageUrl(imageUrl);
        newImage.setType(type);
        newImage.setSort(targetSort);
        newImage.setCreateTime(LocalDateTime.now());
        workImageMapper.insert(newImage);
    }

    @Override
    @Transactional
    public void removeImage(Long workId, Long imageId, Long userId) {
        Work work = workMapper.selectById(workId);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        WorkImage image = workImageMapper.selectById(imageId);
        if (image == null || !image.getWorkId().equals(workId)) {
            throw new RuntimeException("图片不存在或不属于该作品");
        }

        if (image.getStepId() != null) {
            throw new RuntimeException("不能删除步骤关联的图片");
        }

        adjustSortForRemoval(image.getType(), image.getSort(), workId);

        workImageMapper.deleteById(imageId);
    }

    private void cleanupWorkLevelImages(Long workId) {
        workImageMapper.delete(
                new LambdaQueryWrapper<WorkImage>()
                        .eq(WorkImage::getWorkId, workId)
                        .isNull(WorkImage::getStepId)
        );
    }

    private void saveImageGroup(Long workId, List<WorkImageArrangeDTO.ImageItem> items, int type) {
        if (items == null) {
            return;
        }

        List<WorkImageArrangeDTO.ImageItem> sortedItems = items.stream()
                .sorted((a, b) -> {
                    int sortA = a.getSort() != null ? a.getSort() : 0;
                    int sortB = b.getSort() != null ? b.getSort() : 0;
                    return Integer.compare(sortA, sortB);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < sortedItems.size(); i++) {
            WorkImageArrangeDTO.ImageItem item = sortedItems.get(i);
            WorkImage image = new WorkImage();
            image.setWorkId(workId);
            image.setStepId(null);
            image.setImageUrl(item.getImageUrl());
            image.setType(type);
            image.setSort(i);
            image.setCreateTime(LocalDateTime.now());
            workImageMapper.insert(image);
        }
    }

    private void adjustSortForRemoval(int type, int sort, Long workId) {
        workImageMapper.update(null,
                new LambdaUpdateWrapper<WorkImage>()
                        .eq(WorkImage::getWorkId, workId)
                        .eq(WorkImage::getType, type)
                        .isNull(WorkImage::getStepId)
                        .gt(WorkImage::getSort, sort)
                        .setSql("sort = sort - 1")
        );
    }

    private void adjustSortForInsertion(int type, int sort, Long workId) {
        workImageMapper.update(null,
                new LambdaUpdateWrapper<WorkImage>()
                        .eq(WorkImage::getWorkId, workId)
                        .eq(WorkImage::getType, type)
                        .isNull(WorkImage::getStepId)
                        .ge(WorkImage::getSort, sort)
                        .setSql("sort = sort + 1")
        );
    }

    private int getMaxSort(Long workId, int type) {
        List<WorkImage> images = workImageMapper.selectList(
                new LambdaQueryWrapper<WorkImage>()
                        .eq(WorkImage::getWorkId, workId)
                        .eq(WorkImage::getType, type)
                        .isNull(WorkImage::getStepId)
                        .orderByDesc(WorkImage::getSort)
        );

        return images.isEmpty() ? -1 : images.get(0).getSort();
    }
}
