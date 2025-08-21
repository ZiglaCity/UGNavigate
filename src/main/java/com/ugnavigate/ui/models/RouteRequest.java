package com.ugnavigate.ui.models;

import java.util.List;

public class RouteRequest {
    private String start;
    private String destination;
    private List<String> landmarks;
    private String criteria; // "distance" | "time"

    public RouteRequest() {}

    public RouteRequest(String start, String destination, List<String> landmarks, String criteria) {
        this.start = start;
        this.destination = destination;
        this.landmarks = landmarks;
        this.criteria = criteria;
    }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public List<String> getLandmarks() { return landmarks; }
    public void setLandmarks(List<String> landmarks) { this.landmarks = landmarks; }

    public String getCriteria() { return criteria; }
    public void setCriteria(String criteria) { this.criteria = criteria; }
}
