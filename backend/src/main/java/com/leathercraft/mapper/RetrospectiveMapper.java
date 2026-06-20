package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leathercraft.entity.Retrospective;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RetrospectiveMapper extends BaseMapper<Retrospective> {

    @Select("SELECT r.*, w.title as workTitle, w.cover as workCover, w.status as workStatus, " +
            "c.name as categoryName FROM t_retrospective r " +
            "LEFT JOIN t_work w ON r.work_id = w.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "WHERE r.work_id = #{workId} AND r.deleted = 0")
    Retrospective selectByWorkId(@Param("workId") Long workId);

    @Select("SELECT r.*, w.title as workTitle, w.cover as workCover, w.status as workStatus, " +
            "c.name as categoryName FROM t_retrospective r " +
            "LEFT JOIN t_work w ON r.work_id = w.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "WHERE r.user_id = #{userId} AND r.deleted = 0 AND w.deleted = 0 " +
            "ORDER BY r.update_time DESC")
    IPage<Retrospective> selectUserRetrospectives(Page<Retrospective> page, @Param("userId") Long userId);

    @Insert("INSERT INTO t_retrospective (work_id, user_id, rework_points, loss_reasons, improvements, create_time, update_time, deleted) " +
            "VALUES (#{workId}, #{userId}, #{reworkPoints}, #{lossReasons}, #{improvements}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE rework_points = #{reworkPoints}, loss_reasons = #{lossReasons}, " +
            "improvements = #{improvements}, user_id = #{userId}, update_time = NOW(), deleted = 0")
    int upsertByWorkId(Retrospective retrospective);

    @Update("UPDATE t_retrospective SET deleted = 1, update_time = NOW() WHERE work_id = #{workId} AND deleted = 0")
    int logicDeleteByWorkId(@Param("workId") Long workId);
}
