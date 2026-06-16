package com.leathercraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.List;

@TableName("t_work")
public class Work {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String cover;

    private String content;

    private String materials;

    private String materialSummary;

    private String materialBrief;

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
    private List<WorkStep> steps;

    @TableField(exist = false)
    private Boolean isFavorite;

    @TableField(exist = false)
    private List<String> craftHighlights;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public String getMaterialSummary() {
        return materialSummary;
    }

    public void setMaterialSummary(String materialSummary) {
        this.materialSummary = materialSummary;
    }

    public String getMaterialBrief() {
        return materialBrief;
    }

    public void setMaterialBrief(String materialBrief) {
        this.materialBrief = materialBrief;
    }

    public String getCraftSteps() {
        return craftSteps;
    }

    public void setCraftSteps(String craftSteps) {
        this.craftSteps = craftSteps;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCraftTypeId() {
        return craftTypeId;
    }

    public void setCraftTypeId(Long craftTypeId) {
        this.craftTypeId = craftTypeId;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCraftTypeName() {
        return craftTypeName;
    }

    public void setCraftTypeName(String craftTypeName) {
        this.craftTypeName = craftTypeName;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getProcessImages() {
        return processImages;
    }

    public void setProcessImages(List<String> processImages) {
        this.processImages = processImages;
    }

    public List<WorkStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkStep> steps) {
        this.steps = steps;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public List<String> getCraftHighlights() {
        return craftHighlights;
    }

    public void setCraftHighlights(List<String> craftHighlights) {
        this.craftHighlights = craftHighlights;
    }
}
