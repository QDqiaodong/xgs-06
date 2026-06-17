package com.leathercraft.dto;

import java.util.List;

public class WorkImageArrangeDTO {
    private Long workId;
    private List<ImageItem> finishedImages;
    private List<ImageItem> processImages;
    private List<ImageItem> detailImages;

    public static class ImageItem {
        private Long id;
        private String imageUrl;
        private Integer sort;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public List<ImageItem> getFinishedImages() {
        return finishedImages;
    }

    public void setFinishedImages(List<ImageItem> finishedImages) {
        this.finishedImages = finishedImages;
    }

    public List<ImageItem> getProcessImages() {
        return processImages;
    }

    public void setProcessImages(List<ImageItem> processImages) {
        this.processImages = processImages;
    }

    public List<ImageItem> getDetailImages() {
        return detailImages;
    }

    public void setDetailImages(List<ImageItem> detailImages) {
        this.detailImages = detailImages;
    }
}
