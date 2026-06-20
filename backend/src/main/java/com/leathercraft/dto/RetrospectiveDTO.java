package com.leathercraft.dto;

public class RetrospectiveDTO {
    private Long workId;
    private String reworkPoints;
    private String lossReasons;
    private String improvements;

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public String getReworkPoints() {
        return reworkPoints;
    }

    public void setReworkPoints(String reworkPoints) {
        this.reworkPoints = reworkPoints;
    }

    public String getLossReasons() {
        return lossReasons;
    }

    public void setLossReasons(String lossReasons) {
        this.lossReasons = lossReasons;
    }

    public String getImprovements() {
        return improvements;
    }

    public void setImprovements(String improvements) {
        this.improvements = improvements;
    }
}
