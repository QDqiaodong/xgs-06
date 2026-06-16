package com.leathercraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.leathercraft.common.Result;
import com.leathercraft.dto.WorkPublishDTO;
import com.leathercraft.entity.Work;
import com.leathercraft.service.WorkService;
import com.leathercraft.validator.MaterialValidator;
import com.leathercraft.validator.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/work")
public class WorkController {

    @Autowired
    private WorkService workService;

    @Autowired
    private MaterialValidator materialValidator;

    @PostMapping("/validate-materials")
    public Result<ValidationResult> validateMaterials(@RequestBody Map<String, String> body) {
        String materialSummary = body.get("materialSummary");
        String materials = body.get("materials");
        ValidationResult result = workService.validateMaterialsFull(materialSummary, materials);
        return Result.success(result);
    }

    @GetMapping("/material-reference")
    public Result<Map<String, Object>> getMaterialReference() {
        return Result.success(materialValidator.getValidationReferenceData());
    }

    @GetMapping("/page")
    public Result<IPage<Work>> getWorkPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long craftTypeId,
            @RequestParam(required = false) Long userId) {
        return Result.success(workService.getWorkPage(page, size, categoryId, craftTypeId, userId));
    }

    @GetMapping("/{id}")
    public Result<Work> getWorkDetail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        Work work = workService.getWorkDetail(id, userId);
        if (work != null) {
            workService.incrementViewCount(id);
        }
        return Result.success(work);
    }

    @PostMapping
    public Result<Void> publishWork(@RequestBody WorkPublishDTO dto, @RequestParam Long userId) {
        workService.publishWork(dto, userId);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateWork(@RequestBody WorkPublishDTO dto, @RequestParam Long userId) {
        workService.updateWork(dto, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> offlineWork(@PathVariable Long id, @RequestParam Long userId) {
        workService.offlineWork(id, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}/permanent")
    public Result<Void> deleteWork(@PathVariable Long id, @RequestParam Long userId) {
        workService.deleteWork(id, userId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<IPage<Work>> getUserWorks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId) {
        return Result.success(workService.getUserWorks(page, size, userId));
    }

    @GetMapping("/favorite")
    public Result<IPage<Work>> getFavoriteWorks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId) {
        return Result.success(workService.getFavoriteWorks(page, size, userId));
    }

    @GetMapping("/hot")
    public Result<List<Work>> getHotWorks() {
        return Result.success(workService.getHotWorks());
    }
}
