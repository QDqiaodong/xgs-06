package com.leathercraft.dto;

import java.util.List;

public class WorkPublishDTO {
    private Long id;
    private String title;
    private String cover;
    private String content;
    private String materials;
    private String materialSummary;
    private String craftSteps;
    private Long categoryId;
    private Long craftTypeId;
    private List<String> images;
    private List<String> processImages;
    private List<WorkStepDTO> steps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<WorkStepDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkStepDTO> steps) {
        this.steps = steps;
    }
}
