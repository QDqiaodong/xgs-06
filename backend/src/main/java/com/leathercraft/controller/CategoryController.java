package com.leathercraft.controller;

import com.leathercraft.common.Result;
import com.leathercraft.entity.Category;
import com.leathercraft.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getByType(@RequestParam(required = false) String type) {
        return Result.success(categoryService.getByType(type));
    }
}
