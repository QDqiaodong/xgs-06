package com.leathercraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_work_image")
public class WorkImage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long workId;

    private String imageUrl;

    private Integer type;

    private Integer sort;

    private LocalDateTime createTime;
}
