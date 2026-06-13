package com.leathercraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getByType(String type);
}
