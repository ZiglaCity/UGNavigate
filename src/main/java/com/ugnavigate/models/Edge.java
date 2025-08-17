package com.ugnavigate.models;

public class Edge {
    private GraphNode from;
    private GraphNode to;
    private double weight;

    public Edge(GraphNode from, GraphNode to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public GraphNode getFrom() { return from; }
    public GraphNode getTo() { return to; }
    public double getWeight() { return weight; }
}
