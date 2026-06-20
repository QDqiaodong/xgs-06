package com.leathercraft.controller;

import com.leathercraft.common.Result;
import com.leathercraft.entity.Category;
import com.leathercraft.service.CategoryCraftRelationService;
import com.leathercraft.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryCraftRelationService categoryCraftRelationService;

    @GetMapping("/list")
    public Result<List<Category>> getByType(@RequestParam(required = false) String type,
                                            @RequestParam(required = false, defaultValue = "false") boolean includeDisabled) {
        return Result.success(categoryService.getByType(type, includeDisabled));
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @GetMapping("/craft-by-category")
    public Result<List<Category>> getCraftTypesByCategory(@RequestParam(required = false) Long categoryId) {
        return Result.success(categoryCraftRelationService.getCraftTypesByCategoryIdEnhanced(categoryId));
    }

    @GetMapping("/validate-craft")
    public Result<Map<String, Object>> validateCategoryCraftRelation(
            @RequestParam Long categoryId,
            @RequestParam Long craftTypeId) {
        String errorMsg = categoryCraftRelationService.validateCategoryCraftRelation(categoryId, craftTypeId);
        boolean excluded = categoryCraftRelationService.isExcludedRelation(categoryId, craftTypeId);
        if (errorMsg != null) {
            return Result.success(Map.of(
                    "valid", false,
                    "excluded", true,
                    "message", errorMsg
            ));
        }
        return Result.success(Map.of(
                "valid", true,
                "excluded", excluded,
                "message", ""
        ));
    }

    @PostMapping
    public Result<Category> add(@RequestBody Category category) {
        try {
            return Result.success(categoryService.addCategory(category));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Category> update(@RequestBody Category category) {
        try {
            return Result.success(categoryService.updateCategory(category));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-enabled")
    public Result<Void> toggleEnabled(@PathVariable Long id) {
        try {
            categoryService.toggleEnabled(id);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check-name")
    public Result<Boolean> checkNameDuplicate(@RequestParam String type,
                                               @RequestParam String name,
                                               @RequestParam(required = false) Long excludeId) {
        return Result.success(categoryService.checkNameDuplicate(type, name, excludeId));
    }
}
