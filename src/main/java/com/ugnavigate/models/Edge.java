package com.ugnavigate.models;

public class Edge {
    private final GraphNode from;
    private final GraphNode to;
    private final double weight;
    private final double distance;
    private final String direction;

    public Edge(GraphNode from, GraphNode to, double weight, double distance, String direction) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.distance = distance;
        this.direction = direction;
    }

    public GraphNode getFrom() { return from; }
    public GraphNode getTo() { return to; }
    public double getWeight() { return weight; }
    public double getDistance() {
        return distance;
    }
    public String getDirection() {
        return direction;
    }
}
