package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.entity.Category;
import com.leathercraft.entity.CategoryCraftRelation;
import com.leathercraft.mapper.CategoryCraftRelationMapper;
import com.leathercraft.mapper.CategoryMapper;
import com.leathercraft.service.CategoryCraftRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Service
public class CategoryCraftRelationServiceImpl extends ServiceImpl<CategoryCraftRelationMapper, CategoryCraftRelation>
        implements CategoryCraftRelationService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Map<String, Object>> getCraftTypesByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        return baseMapper.selectCraftTypesByCategoryId(categoryId);
    }

    @Override
    public List<Category> getCraftTypesByCategoryIdEnhanced(Long categoryId) {
        List<Category> result = new ArrayList<>();
        if (categoryId == null) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getType, "craft");
            wrapper.eq(Category::getEnabled, 1);
            wrapper.orderByAsc(Category::getSort);
            return categoryMapper.selectList(wrapper);
        }

        List<Map<String, Object>> rows = baseMapper.selectCraftTypesByCategoryId(categoryId);
        Set<Long> foundIds = new HashSet<>();

        for (Map<String, Object> row : rows) {
            Category cat = new Category();
            Number idNum = (Number) row.get("id");
            cat.setId(idNum != null ? idNum.longValue() : null);
            cat.setName((String) row.get("name"));
            cat.setIcon((String) row.get("icon"));
            cat.setType("craft");

            Object sortObj = row.get("sortOrder");
            if (sortObj != null) {
                cat.setSort(((Number) sortObj).intValue());
            }

            String relType = (String) row.get("relationType");
            cat.setEnabled("excluded".equals(relType) ? 0 : 1);

            result.add(cat);
            if (cat.getId() != null) {
                foundIds.add(cat.getId());
            }
        }

        if (result.isEmpty()) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getType, "craft");
            wrapper.eq(Category::getEnabled, 1);
            wrapper.orderByAsc(Category::getSort);
            result = categoryMapper.selectList(wrapper);
        }

        return result;
    }

    @Override
    public String validateCategoryCraftRelation(Long categoryId, Long craftTypeId) {
        if (categoryId == null || craftTypeId == null) {
            return null;
        }
        String relationType = baseMapper.selectRelationType(categoryId, craftTypeId);
        if ("excluded".equals(relationType)) {
            Category category = categoryMapper.selectById(categoryId);
            Category craft = categoryMapper.selectById(craftTypeId);
            String categoryName = category != null ? category.getName() : "该品类";
            String craftName = craft != null ? craft.getName() : "该工艺";
            return "【" + craftName + "】与【" + categoryName + "】的搭配不太合理，"
                    + categoryName + "通常不适用" + craftName + "，请选择更匹配的工艺类型";
        }
        if ("common".equals(relationType)) {
            return null;
        }
        if ("optional".equals(relationType)) {
            return null;
        }
        return null;
    }

    @Override
    public boolean isExcludedRelation(Long categoryId, Long craftTypeId) {
        if (categoryId == null || craftTypeId == null) {
            return false;
        }
        return baseMapper.countExcludedRelation(categoryId, craftTypeId) > 0;
    }
}
