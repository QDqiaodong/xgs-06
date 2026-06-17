package com.leathercraft.dto;

public class WorkImageMoveDTO {
    private Long workId;
    private Long imageId;
    private Integer targetType;
    private Integer targetSort;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Integer getTargetSort() {
        return targetSort;
    }

    public void setTargetSort(Integer targetSort) {
        this.targetSort = targetSort;
    }
}
