package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leathercraft.entity.Work;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkMapper extends BaseMapper<Work> {

    @Select("SELECT w.*, u.nickname, u.avatar, c.name as categoryName, ct.name as craftTypeName " +
            "FROM t_work w LEFT JOIN t_user u ON w.user_id = u.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "LEFT JOIN t_category ct ON w.craft_type_id = ct.id " +
            "WHERE w.status = 1 AND w.deleted = 0 " +
            "AND (#{categoryId} IS NULL OR w.category_id = #{categoryId}) " +
            "AND (#{craftTypeId} IS NULL OR w.craft_type_id = #{craftTypeId}) " +
            "ORDER BY w.create_time DESC")
    IPage<Work> selectWorkPage(Page<Work> page, @Param("categoryId") Long categoryId, @Param("craftTypeId") Long craftTypeId);

    @Select("SELECT w.*, u.nickname, u.avatar, c.name as categoryName, ct.name as craftTypeName " +
            "FROM t_work w LEFT JOIN t_user u ON w.user_id = u.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "LEFT JOIN t_category ct ON w.craft_type_id = ct.id " +
            "WHERE w.id = #{id} AND w.status = 1 AND w.deleted = 0")
    Work selectWorkDetail(@Param("id") Long id);

    @Select("SELECT w.*, u.nickname, u.avatar, c.name as categoryName, ct.name as craftTypeName " +
            "FROM t_work w LEFT JOIN t_user u ON w.user_id = u.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "LEFT JOIN t_category ct ON w.craft_type_id = ct.id " +
            "WHERE w.user_id = #{userId} AND w.deleted = 0 " +
            "ORDER BY w.create_time DESC")
    IPage<Work> selectUserWorks(Page<Work> page, @Param("userId") Long userId);

    @Select("SELECT w.*, u.nickname, u.avatar, c.name as categoryName, ct.name as craftTypeName " +
            "FROM t_work w INNER JOIN t_favorite f ON w.id = f.work_id " +
            "LEFT JOIN t_user u ON w.user_id = u.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "LEFT JOIN t_category ct ON w.craft_type_id = ct.id " +
            "WHERE f.user_id = #{userId} AND w.status = 1 AND w.deleted = 0 " +
            "ORDER BY f.create_time DESC")
    IPage<Work> selectFavoriteWorks(Page<Work> page, @Param("userId") Long userId);

    @Select("SELECT w.*, u.nickname, u.avatar, c.name as categoryName, ct.name as craftTypeName " +
            "FROM t_work w LEFT JOIN t_user u ON w.user_id = u.id " +
            "LEFT JOIN t_category c ON w.category_id = c.id " +
            "LEFT JOIN t_category ct ON w.craft_type_id = ct.id " +
            "WHERE w.status = 1 AND w.deleted = 0 " +
            "ORDER BY w.view_count DESC, w.favorite_count DESC " +
            "LIMIT #{limit}")
    java.util.List<Work> selectHotWorks(@Param("limit") Integer limit);
}
