package com.leathercraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getByType(String type);

    List<Category> getByType(String type, boolean includeDisabled);

    Category addCategory(Category category);

    Category updateCategory(Category category);

    void deleteCategory(Long id);

    void toggleEnabled(Long id);

    boolean checkNameDuplicate(String type, String name, Long excludeId);
}
