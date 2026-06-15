package com.leathercraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leathercraft.entity.WorkStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkStepMapper extends BaseMapper<WorkStep> {

    @Select("SELECT * FROM t_work_step WHERE work_id = #{workId} ORDER BY sort ASC")
    List<WorkStep> selectStepsByWorkId(@Param("workId") Long workId);
}
