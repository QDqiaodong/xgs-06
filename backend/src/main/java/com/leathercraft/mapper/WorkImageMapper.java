package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leathercraft.entity.WorkImage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkImageMapper extends BaseMapper<WorkImage> {

    @Select("SELECT image_url FROM t_work_image WHERE work_id = #{workId} AND type = #{type} AND step_id IS NULL ORDER BY sort ASC")
    List<String> selectImagesByWorkId(@Param("workId") Long workId, @Param("type") Integer type);

    @Select("SELECT image_url FROM t_work_image WHERE step_id = #{stepId} ORDER BY sort ASC")
    List<String> selectImagesByStepId(@Param("stepId") Long stepId);

    @Select("SELECT * FROM t_work_image WHERE work_id = #{workId}")
    List<WorkImage> selectAllByWorkId(@Param("workId") Long workId);

    @Delete("DELETE FROM t_work_image WHERE work_id = #{workId}")
    int deleteByWorkId(@Param("workId") Long workId);

    @Delete("DELETE FROM t_work_image WHERE step_id IN (SELECT id FROM t_work_step WHERE work_id = #{workId})")
    int deleteStepImagesByWorkId(@Param("workId") Long workId);
}
