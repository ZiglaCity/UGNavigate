package com.ugnavigate.models;

public class Neighbor {
    private String neighbor;
    private double distance_m;
    private String direction;
    private double neighbor_lat;
    private double neighbor_lon;

    public String getNeighbor() {
        return neighbor;
    }

    public double getDistanceMeters() {
        return distance_m;
    }

    public String getDirection() {
        return direction;
    }

    public double getNeighborLat() {
        return neighbor_lat;
    }

    public double getNeighborLon() {
        return neighbor_lon;
    }

    @Override
    public String toString() {
        return String.format(
                "%s [%.2f m, Dir: %s, Lat: %.6f, Lon: %.6f]",
                neighbor, distance_m, direction, neighbor_lat, neighbor_lon
        );
    }
}
