package com.leathercraft.controller;

import com.leathercraft.common.Result;
import com.leathercraft.common.UserContext;
import com.leathercraft.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    public Result<Void> toggleFavorite(@RequestParam Long workId) {
        Long currentUserId = UserContext.requireCurrentUserId();
        favoriteService.toggleFavorite(currentUserId, workId);
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long workId) {
        Long currentUserId = UserContext.requireCurrentUserId();
        return Result.success(favoriteService.isFavorite(currentUserId, workId));
    }
}
