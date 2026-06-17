package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.entity.Category;
import com.leathercraft.mapper.CategoryMapper;
import com.leathercraft.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getByType(String type) {
        return getByType(type, false);
    }

    @Override
    public List<Category> getByType(String type, boolean includeDisabled) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Category::getType, type);
        }
        if (!includeDisabled) {
            wrapper.eq(Category::getEnabled, 1);
        }
        wrapper.orderByAsc(Category::getSort);
        return list(wrapper);
    }

    @Override
    public Category addCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (category.getType() == null || category.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("分类类型不能为空");
        }
        if (checkNameDuplicate(category.getType(), category.getName(), null)) {
            throw new IllegalArgumentException("该分类名称已存在");
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getEnabled() == null) {
            category.setEnabled(1);
        }
        save(category);
        return category;
    }

    @Override
    public Category updateCategory(Category category) {
        if (category.getId() == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        Category existing = getById(category.getId());
        if (existing == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        if (category.getName() != null && !category.getName().trim().isEmpty()) {
            if (checkNameDuplicate(category.getType() != null ? category.getType() : existing.getType(),
                    category.getName(), category.getId())) {
                throw new IllegalArgumentException("该分类名称已存在");
            }
        }
        updateById(category);
        return getById(category.getId());
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        removeById(id);
    }

    @Override
    public void toggleEnabled(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        category.setEnabled(category.getEnabled() == 1 ? 0 : 1);
        updateById(category);
    }

    @Override
    public boolean checkNameDuplicate(String type, String name, Long excludeId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getType, type);
        wrapper.eq(Category::getName, name);
        if (excludeId != null) {
            wrapper.ne(Category::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
}
