package com.leathercraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.entity.Favorite;

public interface FavoriteService extends IService<Favorite> {
    void toggleFavorite(Long userId, Long workId);
    boolean isFavorite(Long userId, Long workId);
}
