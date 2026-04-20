package com.middleware.org.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程节点 DTO
 */
public class StageDTO {

    private String stage;
    private String name;
    private String status;
    private Long startTime;
    private Long endTime;

    public StageDTO() {}

    public StageDTO(String stage, String name, String status) {
        this.stage = stage;
        this.name = name;
        this.status = status;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("stage", stage);
        map.put("name", name);
        map.put("status", status);
        if (startTime != null) map.put("startTime", startTime);
        if (endTime != null) map.put("endTime", endTime);
        return map;
    }
}