package com.leathercraft.dto;

public class RetrospectiveDTO {
    private Long workId;
    private String reworkPoints;
    private String reworkReason;
    private String occurrenceStage;
    private String handleResult;
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

    public String getReworkReason() {
        return reworkReason;
    }

    public void setReworkReason(String reworkReason) {
        this.reworkReason = reworkReason;
    }

    public String getOccurrenceStage() {
        return occurrenceStage;
    }

    public void setOccurrenceStage(String occurrenceStage) {
        this.occurrenceStage = occurrenceStage;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
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
