package com.ugnavigate.models;

import java.util.List;

public class Route {
    private final List<String> path;
    private final double distanceKm;
    private final double estimatedTimeMin;

    public Route(List<String> path, double distanceKm, double estimatedTimeMin) {
        this.path = path;
        this.distanceKm = distanceKm;
        this.estimatedTimeMin = estimatedTimeMin;
    }

    public List<String> getPath() { return path; }
    public double getDistanceKm() { return distanceKm; }
    public double getEstimatedTimeMin() { return estimatedTimeMin; }
}
