package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leathercraft.entity.WorkImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkImageMapper extends BaseMapper<WorkImage> {

    @Select("SELECT image_url FROM t_work_image WHERE work_id = #{workId} AND type = #{type} ORDER BY sort ASC")
    List<String> selectImagesByWorkId(@Param("workId") Long workId, @Param("type") Integer type);
}
