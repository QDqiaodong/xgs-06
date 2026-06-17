package com.leathercraft.controller;

import com.leathercraft.common.Result;
import com.leathercraft.common.UserContext;
import com.leathercraft.dto.WorkImageArrangeDTO;
import com.leathercraft.dto.WorkImageMoveDTO;
import com.leathercraft.entity.WorkImage;
import com.leathercraft.service.WorkImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/work-image")
public class WorkImageController {

    @Autowired
    private WorkImageService workImageService;

    @GetMapping("/{workId}")
    public Result<Map<Integer, List<WorkImage>>> getImagesGroupedByType(@PathVariable Long workId) {
        return Result.success(workImageService.getImagesGroupedByType(workId));
    }

    @PostMapping("/arrange")
    public Result<Void> arrangeImages(@RequestBody WorkImageArrangeDTO dto) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workImageService.arrangeImages(dto, currentUserId);
        return Result.success();
    }

    @PostMapping("/move")
    public Result<Void> moveImage(@RequestBody WorkImageMoveDTO dto) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workImageService.moveImage(dto, currentUserId);
        return Result.success();
    }

    @PostMapping("/sort")
    public Result<Void> updateImageSort(
            @RequestParam Long workId,
            @RequestParam Long imageId,
            @RequestParam Integer sort) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workImageService.updateImageSort(workId, imageId, sort, currentUserId);
        return Result.success();
    }

    @PostMapping
    public Result<Void> addImage(
            @RequestParam Long workId,
            @RequestParam String imageUrl,
            @RequestParam Integer type,
            @RequestParam(required = false) Integer sort) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workImageService.addImage(workId, imageUrl, type, sort, currentUserId);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> removeImage(
            @RequestParam Long workId,
            @RequestParam Long imageId) {
        Long currentUserId = UserContext.requireCurrentUserId();
        workImageService.removeImage(workId, imageId, currentUserId);
        return Result.success();
    }
}
