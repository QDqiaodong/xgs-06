package com.leathercraft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.leathercraft.common.Result;
import com.leathercraft.common.UserContext;
import com.leathercraft.dto.RetrospectiveDTO;
import com.leathercraft.entity.Retrospective;
import com.leathercraft.service.RetrospectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retrospective")
public class RetrospectiveController {

    @Autowired
    private RetrospectiveService retrospectiveService;

    @GetMapping("/work/{workId}")
    public Result<Retrospective> getByWorkId(@PathVariable Long workId) {
        return Result.success(retrospectiveService.getByWorkId(workId));
    }

    @GetMapping("/my")
    public Result<IPage<Retrospective>> getMyRetrospectives(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = UserContext.requireCurrentUserId();
        return Result.success(retrospectiveService.getUserRetrospectives(page, size, currentUserId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody RetrospectiveDTO dto) {
        Long currentUserId = UserContext.requireCurrentUserId();
        retrospectiveService.saveRetrospective(dto, currentUserId);
        return Result.success();
    }

    @DeleteMapping("/work/{workId}")
    public Result<Void> delete(@PathVariable Long workId) {
        Long currentUserId = UserContext.requireCurrentUserId();
        retrospectiveService.deleteRetrospective(workId, currentUserId);
        return Result.success();
    }
}
