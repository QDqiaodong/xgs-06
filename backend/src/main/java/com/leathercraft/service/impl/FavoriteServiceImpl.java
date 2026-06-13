package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.entity.Favorite;
import com.leathercraft.entity.Work;
import com.leathercraft.mapper.FavoriteMapper;
import com.leathercraft.mapper.WorkMapper;
import com.leathercraft.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private WorkMapper workMapper;

    @Override
    @Transactional
    public void toggleFavorite(Long userId, Long workId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getWorkId, workId);
        Favorite favorite = getOne(wrapper);

        Work work = workMapper.selectById(workId);

        if (favorite != null) {
            removeById(favorite.getId());
            if (work != null && work.getFavoriteCount() > 0) {
                work.setFavoriteCount(work.getFavoriteCount() - 1);
                workMapper.updateById(work);
            }
        } else {
            Favorite newFavorite = new Favorite();
            newFavorite.setUserId(userId);
            newFavorite.setWorkId(workId);
            newFavorite.setCreateTime(LocalDateTime.now());
            save(newFavorite);
            if (work != null) {
                work.setFavoriteCount(work.getFavoriteCount() + 1);
                workMapper.updateById(work);
            }
        }
    }

    @Override
    public boolean isFavorite(Long userId, Long workId) {
        return baseMapper.existsByUserIdAndWorkId(userId, workId);
    }
}
