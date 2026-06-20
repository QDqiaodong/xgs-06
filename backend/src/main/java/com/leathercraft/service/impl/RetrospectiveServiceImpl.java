package com.leathercraft.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leathercraft.dto.RetrospectiveDTO;
import com.leathercraft.entity.Retrospective;
import com.leathercraft.entity.Work;
import com.leathercraft.mapper.RetrospectiveMapper;
import com.leathercraft.mapper.WorkMapper;
import com.leathercraft.service.RetrospectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetrospectiveServiceImpl extends ServiceImpl<RetrospectiveMapper, Retrospective> implements RetrospectiveService {

    @Autowired
    private WorkMapper workMapper;

    @Override
    public Retrospective getByWorkId(Long workId) {
        if (workId == null || workId <= 0) {
            return null;
        }
        return baseMapper.selectByWorkId(workId);
    }

    @Override
    public IPage<Retrospective> getUserRetrospectives(Integer page, Integer size, Long userId) {
        long current = page == null || page < 1 ? 1 : page;
        long pageSize = size == null || size < 1 ? 10 : Math.min(size, 50);
        Page<Retrospective> pageParam = new Page<>(current, pageSize);
        IPage<Retrospective> result = baseMapper.selectUserRetrospectives(pageParam, userId);
        if (result.getRecords().isEmpty()
                && result.getTotal() > 0
                && result.getPages() > 0
                && result.getCurrent() > result.getPages()) {
            result.setTotal(0);
        }
        return result;
    }

    @Override
    @Transactional
    public void saveRetrospective(RetrospectiveDTO dto, Long userId) {
        if (dto == null || dto.getWorkId() == null) {
            throw new RuntimeException("缺少作品ID");
        }
        Work work = workMapper.selectById(dto.getWorkId());
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限为该作品添加复盘记录");
        }

        Retrospective retrospective = new Retrospective();
        retrospective.setWorkId(dto.getWorkId());
        retrospective.setUserId(userId);
        retrospective.setReworkPoints(trimOrEmpty(dto.getReworkPoints()));
        retrospective.setLossReasons(trimOrEmpty(dto.getLossReasons()));
        retrospective.setImprovements(trimOrEmpty(dto.getImprovements()));
        baseMapper.upsertByWorkId(retrospective);
    }

    @Override
    @Transactional
    public void deleteRetrospective(Long workId, Long userId) {
        if (workId == null || workId <= 0) {
            throw new RuntimeException("参数无效");
        }
        Work work = workMapper.selectById(workId);
        if (work == null || !work.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除该复盘记录");
        }
        baseMapper.logicDeleteByWorkId(workId);
    }

    private String trimOrEmpty(String text) {
        return text == null ? "" : text.trim();
    }
}
