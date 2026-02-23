package com.example.templeinfo;

public class TempleIdRangeRequest {
    private Long startId;
    private Long endId;

    public TempleIdRangeRequest() {
    }

    public TempleIdRangeRequest(Long startId, Long endId) {
        this.startId = startId;
        this.endId = endId;
    }

    public Long getStartId() {
        return startId;
    }

    public void setStartId(Long startId) {
        this.startId = startId;
    }

    public Long getEndId() {
        return endId;
    }

    public void setEndId(Long endId) {
        this.endId = endId;
    }
}
