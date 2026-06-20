package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leathercraft.entity.CategoryCraftRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryCraftRelationMapper extends BaseMapper<CategoryCraftRelation> {

    @Select("SELECT c.id, c.name, c.icon, r.relation_type AS relationType, r.sort AS sortOrder " +
            "FROM t_category_craft_relation r " +
            "INNER JOIN t_category c ON r.craft_type_id = c.id " +
            "WHERE r.category_id = #{categoryId} " +
            "AND c.enabled = 1 " +
            "ORDER BY r.sort ASC, c.sort ASC")
    List<Map<String, Object>> selectCraftTypesByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT relation_type FROM t_category_craft_relation " +
            "WHERE category_id = #{categoryId} AND craft_type_id = #{craftTypeId} " +
            "LIMIT 1")
    String selectRelationType(@Param("categoryId") Long categoryId, @Param("craftTypeId") Long craftTypeId);

    @Select("SELECT COUNT(*) FROM t_category_craft_relation " +
            "WHERE category_id = #{categoryId} AND craft_type_id = #{craftTypeId} " +
            "AND relation_type = 'excluded'")
    int countExcludedRelation(@Param("categoryId") Long categoryId, @Param("craftTypeId") Long craftTypeId);
}
