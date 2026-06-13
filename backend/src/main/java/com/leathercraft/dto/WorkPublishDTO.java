package com.leathercraft.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkPublishDTO {
    private Long id;
    private String title;
    private String cover;
    private String content;
    private String materials;
    private String craftSteps;
    private Long categoryId;
    private Long craftTypeId;
    private List<String> images;
    private List<String> processImages;
}
