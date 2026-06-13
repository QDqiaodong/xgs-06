package com.leathercraft.controller;

import com.leathercraft.common.Result;
import com.leathercraft.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    public Result<Void> toggleFavorite(@RequestParam Long userId, @RequestParam Long workId) {
        favoriteService.toggleFavorite(userId, workId);
        return Result.success();
    }

    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long workId) {
        return Result.success(favoriteService.isFavorite(userId, workId));
    }
}
