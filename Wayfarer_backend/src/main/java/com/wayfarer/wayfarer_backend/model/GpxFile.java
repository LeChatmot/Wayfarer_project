package com.wayfarer.wayfarer_backend.model;

public class GpxFile {

    private String id;

    private String hikeId;

    private String content;

    public String getHikeId() {
        return hikeId;
    }

    public void setHikeId(String hikeId) {
        this.hikeId = hikeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}