package com.ugnavigate.ui.models;

import java.util.List;
import java.util.Objects;

public class RouteResult {
    private final int routeId;
    private final List<String> path;
    private final double distanceKm;
    private final double estimatedTimeMin;
    private final List<String> landmarks;

    public RouteResult(int routeId, List<String> path, double distanceKm, double estimatedTimeMin, List<String> landmarks) {
        this.routeId = routeId;
        this.path = path;
        this.distanceKm = distanceKm;
        this.estimatedTimeMin = estimatedTimeMin;
        this.landmarks = landmarks;
    }

    public int getRouteId() { return routeId; }
    public List<String> getPath() { return path; }
    public double getDistanceKm() { return distanceKm; }
    public double getEstimatedTimeMin() { return estimatedTimeMin; }
    public List<String> getLandmarks() { return landmarks; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteResult that = (RouteResult) o;
        return routeId == that.routeId && Double.compare(that.distanceKm, distanceKm) == 0 &&
                Double.compare(that.estimatedTimeMin, estimatedTimeMin) == 0 &&
                Objects.equals(path, that.path) && Objects.equals(landmarks, that.landmarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId, path, distanceKm, estimatedTimeMin, landmarks);
    }
}
