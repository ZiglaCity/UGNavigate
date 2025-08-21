package com.ugnavigate.models;

import java.util.ArrayList;
import java.util.List;

public class GraphNode {
    private final String id;
    private Landmark landmark;
    private final List<Edge> neighbors = new ArrayList<>();

//    will be using the landmark name as id
    public GraphNode(String id) {
        this.id = id;
    }

    public String getName(){
        return this.id;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public List<Edge> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Edge neighbor) {
        neighbors.add(neighbor);
    }

    public String getId() {
        return id;
    }
}
