package com.leathercraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.entity.CategoryCraftRelation;
import com.leathercraft.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryCraftRelationService extends IService<CategoryCraftRelation> {

    List<Map<String, Object>> getCraftTypesByCategoryId(Long categoryId);

    List<Category> getCraftTypesByCategoryIdEnhanced(Long categoryId);

    String validateCategoryCraftRelation(Long categoryId, Long craftTypeId);

    boolean isExcludedRelation(Long categoryId, Long craftTypeId);
}
