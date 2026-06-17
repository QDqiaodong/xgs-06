package com.leathercraft.controller;

import com.leathercraft.common.Result;
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
    public Result<Void> arrangeImages(@RequestBody WorkImageArrangeDTO dto, @RequestParam Long userId) {
        workImageService.arrangeImages(dto, userId);
        return Result.success();
    }

    @PostMapping("/move")
    public Result<Void> moveImage(@RequestBody WorkImageMoveDTO dto, @RequestParam Long userId) {
        workImageService.moveImage(dto, userId);
        return Result.success();
    }

    @PostMapping("/sort")
    public Result<Void> updateImageSort(
            @RequestParam Long workId,
            @RequestParam Long imageId,
            @RequestParam Integer sort,
            @RequestParam Long userId) {
        workImageService.updateImageSort(workId, imageId, sort, userId);
        return Result.success();
    }

    @PostMapping
    public Result<Void> addImage(
            @RequestParam Long workId,
            @RequestParam String imageUrl,
            @RequestParam Integer type,
            @RequestParam(required = false) Integer sort,
            @RequestParam Long userId) {
        workImageService.addImage(workId, imageUrl, type, sort, userId);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> removeImage(
            @RequestParam Long workId,
            @RequestParam Long imageId,
            @RequestParam Long userId) {
        workImageService.removeImage(workId, imageId, userId);
        return Result.success();
    }
}
