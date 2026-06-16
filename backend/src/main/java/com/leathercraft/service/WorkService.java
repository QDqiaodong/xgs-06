package com.leathercraft.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.dto.CraftProfileDTO;
import com.leathercraft.dto.WorkPublishDTO;
import com.leathercraft.entity.Work;
import com.leathercraft.validator.ValidationResult;

import java.util.List;

public interface WorkService extends IService<Work> {
    IPage<Work> getWorkPage(Integer page, Integer size, Long categoryId, Long craftTypeId, Long userId);
    Work getWorkDetail(Long id, Long userId);
    void publishWork(WorkPublishDTO dto, Long userId);
    void updateWork(WorkPublishDTO dto, Long userId);
    void offlineWork(Long id, Long userId);
    void deleteWork(Long id, Long userId);
    IPage<Work> getUserWorks(Integer page, Integer size, Long userId);
    IPage<Work> getFavoriteWorks(Integer page, Integer size, Long userId);
    List<Work> getHotWorks();
    void incrementViewCount(Long id);
    CraftProfileDTO getUserCraftProfile(Long userId);
    ValidationResult validateMaterialsFull(String materialSummary, String materialsText);
}
