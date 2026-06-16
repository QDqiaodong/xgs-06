package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leathercraft.entity.Work;
import com.leathercraft.dto.CraftProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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
            "WHERE w.id = #{id} AND w.deleted = 0")
    Work selectWorkDetailAnyStatus(@Param("id") Long id);

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

    @Select("SELECT c.id, c.name, COUNT(*) as count FROM t_work w " +
            "JOIN t_category c ON w.category_id = c.id " +
            "WHERE w.user_id = #{userId} AND w.status = 1 AND w.deleted = 0 " +
            "GROUP BY w.category_id, c.id, c.name " +
            "ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> selectTopCategories(@Param("userId") Long userId);

    @Select("SELECT ct.id, ct.name, COUNT(*) as count FROM t_work w " +
            "JOIN t_category ct ON w.craft_type_id = ct.id " +
            "WHERE w.user_id = #{userId} AND w.status = 1 AND w.deleted = 0 " +
            "GROUP BY w.craft_type_id, ct.id, ct.name " +
            "ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> selectTopCraftTypes(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) as cnt FROM t_work " +
            "WHERE user_id = #{userId} AND status = 1 AND deleted = 0 " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
    Integer selectRecentCount(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) as cnt FROM t_work " +
            "WHERE user_id = #{userId} AND status = 1 AND deleted = 0")
    Integer selectTotalCount(@Param("userId") Long userId);

    @Select("SELECT w.materials, w.material_summary FROM t_work w " +
            "WHERE w.user_id = #{userId} AND w.status = 1 AND w.deleted = 0 " +
            "AND w.materials IS NOT NULL AND w.materials != '' " +
            "ORDER BY w.create_time DESC LIMIT 20")
    List<Map<String, Object>> selectMaterialsForProfile(@Param("userId") Long userId);
}
