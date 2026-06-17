package com.leathercraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.leathercraft.common.Result;
import com.leathercraft.common.UserContext;
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
            @RequestParam(required = false) Long craftTypeId) {
        Long currentUserId = UserContext.getCurrentUserId();
        return Result.success(workService.getWorkPage(page, size, categoryId, craftTypeId, currentUserId));
    }

    @GetMapping("/{id}")
    public Result<Work> getWorkDetail(@PathVariable Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        Work work = workService.getWorkDetail(id, currentUserId);
        if (work != null) {
            workService.incrementViewCount(id);
        }
        return Result.success(work);
    }

    @PostMapping
    public Result<Void> publishWork(@RequestBody WorkPublishDTO dto) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workService.publishWork(dto, currentUserId);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateWork(@RequestBody WorkPublishDTO dto) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workService.updateWork(dto, currentUserId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> offlineWork(@PathVariable Long id) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workService.offlineWork(id, currentUserId);
        return Result.success();
    }

    @PutMapping("/{id}/online")
    public Result<Void> onlineWork(@PathVariable Long id) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workService.onlineWork(id, currentUserId);
        return Result.success();
    }

    @DeleteMapping("/{id}/permanent")
    public Result<Void> deleteWork(@PathVariable Long id) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workService.deleteWork(id, currentUserId);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<IPage<Work>> getUserWorks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = UserContext.requireCurrentUserId();
        return Result.success(workService.getUserWorks(page, size, currentUserId));
    }

    @GetMapping("/favorite")
    public Result<IPage<Work>> getFavoriteWorks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = UserContext.requireCurrentUserId();
        return Result.success(workService.getFavoriteWorks(page, size, currentUserId));
    }

    @GetMapping("/hot")
    public Result<List<Work>> getHotWorks() {
        return Result.success(workService.getHotWorks());
    }
}
