package com.leathercraft.dto;

import java.util.List;

public class CraftProfileDTO {

    private List<CategoryStat> topCategories;
    private List<String> commonMaterials;
    private List<CategoryStat> topCraftTypes;
    private Integer recentCompleted;
    private Integer totalWorks;

    private List<CategoryStat> categoryStats;
    private List<CategoryStat> craftTypeStats;
    private List<WorkStatusStat> workStatusStats;

    private List<MaterialStat> leatherTypeStats;
    private List<MaterialStat> hardwareStats;

    public List<CategoryStat> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<CategoryStat> topCategories) {
        this.topCategories = topCategories;
    }

    public List<String> getCommonMaterials() {
        return commonMaterials;
    }

    public void setCommonMaterials(List<String> commonMaterials) {
        this.commonMaterials = commonMaterials;
    }

    public List<CategoryStat> getTopCraftTypes() {
        return topCraftTypes;
    }

    public void setTopCraftTypes(List<CategoryStat> topCraftTypes) {
        this.topCraftTypes = topCraftTypes;
    }

    public Integer getRecentCompleted() {
        return recentCompleted;
    }

    public void setRecentCompleted(Integer recentCompleted) {
        this.recentCompleted = recentCompleted;
    }

    public Integer getTotalWorks() {
        return totalWorks;
    }

    public void setTotalWorks(Integer totalWorks) {
        this.totalWorks = totalWorks;
    }

    public List<CategoryStat> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<CategoryStat> categoryStats) {
        this.categoryStats = categoryStats;
    }

    public List<CategoryStat> getCraftTypeStats() {
        return craftTypeStats;
    }

    public void setCraftTypeStats(List<CategoryStat> craftTypeStats) {
        this.craftTypeStats = craftTypeStats;
    }

    public List<WorkStatusStat> getWorkStatusStats() {
        return workStatusStats;
    }

    public void setWorkStatusStats(List<WorkStatusStat> workStatusStats) {
        this.workStatusStats = workStatusStats;
    }

    public List<MaterialStat> getLeatherTypeStats() {
        return leatherTypeStats;
    }

    public void setLeatherTypeStats(List<MaterialStat> leatherTypeStats) {
        this.leatherTypeStats = leatherTypeStats;
    }

    public List<MaterialStat> getHardwareStats() {
        return hardwareStats;
    }

    public void setHardwareStats(List<MaterialStat> hardwareStats) {
        this.hardwareStats = hardwareStats;
    }

    public static class CategoryStat {
        private Long id;
        private String name;
        private Integer count;

        public CategoryStat() {}

        public CategoryStat(Long id, String name, Integer count) {
            this.id = id;
            this.name = name;
            this.count = count;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    public static class WorkStatusStat {
        private String status;
        private String statusName;
        private Integer count;

        public WorkStatusStat() {}

        public WorkStatusStat(String status, String statusName, Integer count) {
            this.status = status;
            this.statusName = statusName;
            this.count = count;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    public static class MaterialStat {
        private String name;
        private Integer count;
        private String category;

        public MaterialStat() {}

        public MaterialStat(String name, Integer count) {
            this.name = name;
            this.count = count;
        }

        public MaterialStat(String name, Integer count, String category) {
            this.name = name;
            this.count = count;
            this.category = category;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
