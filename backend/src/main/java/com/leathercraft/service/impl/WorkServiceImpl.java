package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.dto.WorkPublishDTO;
import com.leathercraft.entity.Work;
import com.leathercraft.entity.WorkImage;
import com.leathercraft.mapper.WorkImageMapper;
import com.leathercraft.mapper.WorkMapper;
import com.leathercraft.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WorkServiceImpl extends ServiceImpl<WorkMapper, Work> implements WorkService {

    @Autowired
    private WorkImageMapper workImageMapper;

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String HOT_WORKS_KEY = "hot:works";
    private static final String WORK_VIEW_KEY = "work:view:";

    @Override
    public IPage<Work> getWorkPage(Integer page, Integer size, Long categoryId, Long craftTypeId, Long userId) {
        Page<Work> pageParam = new Page<>(page, size);
        IPage<Work> result = baseMapper.selectWorkPage(pageParam, categoryId, craftTypeId);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1));
            if (userId != null) {
                work.setIsFavorite(checkFavorite(userId, work.getId()));
            }
        });
        return result;
    }

    @Override
    public Work getWorkDetail(Long id, Long userId) {
        Work work = baseMapper.selectWorkDetail(id);
        if (work != null) {
            work.setImages(workImageMapper.selectImagesByWorkId(id, 1));
            work.setProcessImages(workImageMapper.selectImagesByWorkId(id, 2));
            if (userId != null) {
                work.setIsFavorite(checkFavorite(userId, id));
            }
        }
        return work;
    }

    @Override
    @Transactional
    public void publishWork(WorkPublishDTO dto, Long userId) {
        Work work = new Work();
        work.setUserId(userId);
        work.setTitle(dto.getTitle());
        work.setCover(dto.getCover());
        work.setContent(dto.getContent());
        work.setMaterials(dto.getMaterials());
        work.setCraftSteps(dto.getCraftSteps());
        work.setCategoryId(dto.getCategoryId());
        work.setCraftTypeId(dto.getCraftTypeId());
        work.setViewCount(0);
        work.setFavoriteCount(0);
        work.setStatus(1);
        work.setCreateTime(LocalDateTime.now());
        work.setUpdateTime(LocalDateTime.now());
        save(work);

        saveWorkImages(work.getId(), dto.getImages(), 1);
        saveWorkImages(work.getId(), dto.getProcessImages(), 2);
    }

    @Override
    @Transactional
    public void updateWork(WorkPublishDTO dto, Long userId) {
        Work work = getById(dto.getId());
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }
        work.setTitle(dto.getTitle());
        work.setCover(dto.getCover());
        work.setContent(dto.getContent());
        work.setMaterials(dto.getMaterials());
        work.setCraftSteps(dto.getCraftSteps());
        work.setCategoryId(dto.getCategoryId());
        work.setCraftTypeId(dto.getCraftTypeId());
        work.setUpdateTime(LocalDateTime.now());
        updateById(work);

        workImageMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WorkImage>()
                .eq(WorkImage::getWorkId, dto.getId()));

        saveWorkImages(work.getId(), dto.getImages(), 1);
        saveWorkImages(work.getId(), dto.getProcessImages(), 2);
    }

    @Override
    public void offlineWork(Long id, Long userId) {
        Work work = getById(id);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }
        work.setStatus(0);
        work.setUpdateTime(LocalDateTime.now());
        updateById(work);
    }

    @Override
    public IPage<Work> getUserWorks(Integer page, Integer size, Long userId) {
        Page<Work> pageParam = new Page<>(page, size);
        IPage<Work> result = baseMapper.selectUserWorks(pageParam, userId);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1));
        });
        return result;
    }

    @Override
    public IPage<Work> getFavoriteWorks(Integer page, Integer size, Long userId) {
        Page<Work> pageParam = new Page<>(page, size);
        IPage<Work> result = baseMapper.selectFavoriteWorks(pageParam, userId);
        result.getRecords().forEach(work -> {
            work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1));
            work.setIsFavorite(true);
        });
        return result;
    }

    @Override
    public List<Work> getHotWorks() {
        List<Work> hotWorks = baseMapper.selectHotWorks(10);
        hotWorks.forEach(work -> work.setImages(workImageMapper.selectImagesByWorkId(work.getId(), 1)));
        return hotWorks;
    }

    @Override
    public void incrementViewCount(Long id) {
        String key = WORK_VIEW_KEY + id;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
        
        Work work = getById(id);
        if (work != null) {
            work.setViewCount(work.getViewCount() + 1);
            updateById(work);
        }
    }

    private void saveWorkImages(Long workId, List<String> images, Integer type) {
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                WorkImage workImage = new WorkImage();
                workImage.setWorkId(workId);
                workImage.setImageUrl(images.get(i));
                workImage.setType(type);
                workImage.setSort(i);
                workImage.setCreateTime(LocalDateTime.now());
                workImageMapper.insert(workImage);
            }
        }
    }

    private boolean checkFavorite(Long userId, Long workId) {
        return favoriteService.isFavorite(userId, workId);
    }
}
