package com.leathercraft.service;

import com.leathercraft.dto.WorkImageArrangeDTO;
import com.leathercraft.dto.WorkImageMoveDTO;
import com.leathercraft.entity.WorkImage;

import java.util.List;
import java.util.Map;

public interface WorkImageService {
    Map<Integer, List<WorkImage>> getImagesGroupedByType(Long workId);

    void arrangeImages(WorkImageArrangeDTO dto, Long userId);

    void moveImage(WorkImageMoveDTO dto, Long userId);

    void updateImageSort(Long workId, Long imageId, Integer sort, Long userId);

    void addImage(Long workId, String imageUrl, Integer type, Integer sort, Long userId);

    void removeImage(Long workId, Long imageId, Long userId);
}
