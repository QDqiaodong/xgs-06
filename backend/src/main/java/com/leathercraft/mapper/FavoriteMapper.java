package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leathercraft.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("SELECT COUNT(*) > 0 FROM t_favorite WHERE user_id = #{userId} AND work_id = #{workId}")
    boolean existsByUserIdAndWorkId(@Param("userId") Long userId, @Param("workId") Long workId);
}
