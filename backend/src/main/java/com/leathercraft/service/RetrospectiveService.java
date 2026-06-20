package com.leathercraft.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leathercraft.dto.RetrospectiveDTO;
import com.leathercraft.entity.Retrospective;

public interface RetrospectiveService extends IService<Retrospective> {
    Retrospective getByWorkId(Long workId);
    IPage<Retrospective> getUserRetrospectives(Integer page, Integer size, Long userId);
    void saveRetrospective(RetrospectiveDTO dto, Long userId);
    void deleteRetrospective(Long workId, Long userId);
}
