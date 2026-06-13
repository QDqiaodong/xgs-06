package com.leathercraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("t_work")
public class Work {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String cover;

    private String content;

    private String materials;

    private String craftSteps;

    private Long categoryId;

    private Long craftTypeId;

    private Integer viewCount;

    private Integer favoriteCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String avatar;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String craftTypeName;

    @TableField(exist = false)
    private List<String> images;

    @TableField(exist = false)
    private List<String> processImages;

    @TableField(exist = false)
    private Boolean isFavorite;
}
