package com.ugnavigate.algorithms.dijkstra;

import com.ugnavigate.models.Edge;
import com.ugnavigate.models.GraphNode;

import java.util.*;

public class Dijkstra {
    private final GraphNode start;
    private final GraphNode destination;
    private final Map<GraphNode, Double> visited;
    private final Map<GraphNode, Double> unvisited;
    private final Map<GraphNode, GraphNode> to_fromPathBuilder;
    private double shortestDistance;
    private List<GraphNode> shortestPath;
    private String pathSummary;

    public Dijkstra(GraphNode start, GraphNode destination) {
        validateLocation(start, destination);

        this.start = start;
        this.destination = destination;
        this.visited = new HashMap<>();
        this.unvisited = new HashMap<>();
        this.to_fromPathBuilder = new HashMap<>();
        this.shortestPath = new ArrayList<>();
        this.shortestDistance = Double.POSITIVE_INFINITY;

        for (Edge edge : start.getNeighbors()) {
            unvisited.put(edge.getTo(), edge.getDistance());
            to_fromPathBuilder.put(edge.getTo(), start);
        }
        visited.put(start, 0.0);
    }

    private void validateLocation(GraphNode start, GraphNode destination) {
        if (start == null || destination == null) {
            throw new IllegalArgumentException("Invalid path provided! Start or destination is null.");
        }
    }

    public void solve() {
        if (start.equals(destination)) {
            shortestDistance = 0.0;
            shortestPath = List.of(start);
            pathSummary = "Start and destination are the same.";
            return;
        }

        recurse(start, 0.0);

        buildPath();
    }

    private void recurse(GraphNode current, double currentDist) {
        if (current.equals(destination)) {
            shortestDistance = currentDist;
            return;
        }

//        relax neighbors
        for (Edge edge : current.getNeighbors()) {
            GraphNode neighbor = edge.getTo();
            if (!visited.containsKey(neighbor)) {
                double newDist = currentDist + edge.getDistance();
                double oldDist = unvisited.getOrDefault(neighbor, Double.POSITIVE_INFINITY);

                if (newDist < oldDist) {
                    unvisited.put(neighbor, newDist);
                    to_fromPathBuilder.put(neighbor, current);
                }
            }
        }

        GraphNode next = null;
        double smallest = Double.POSITIVE_INFINITY;
        for (Map.Entry<GraphNode, Double> entry : unvisited.entrySet()) {
            if (entry.getValue() < smallest) {
                smallest = entry.getValue();
                next = entry.getKey();
            }
        }

        if (next == null) return;

        unvisited.remove(next);
        visited.put(next, smallest);
        recurse(next, smallest);
    }

    private void buildPath() {
        if (shortestDistance == Double.POSITIVE_INFINITY) {
            pathSummary = "No path found from " + start.getId() + " to " + destination.getId();
            return;
        }

        List<GraphNode> path = new ArrayList<>();
        GraphNode cur = destination;

        while (cur != null) {
            path.add(cur);
            cur = to_fromPathBuilder.get(cur);
        }

        Collections.reverse(path);
        shortestPath = path;

        StringBuilder sb = new StringBuilder();
        sb.append("From ").append(start.getLandmark().getName());
        for (GraphNode node : shortestPath) {
            if (!node.equals(start)) {
                sb.append(" --> ").append(node.getLandmark().getName());
            }
        }
        pathSummary = sb.toString();
    }

    public double getShortestDistance() {
        return shortestDistance;
    }

    public List<GraphNode> getShortestPath() {
        return shortestPath;
    }

    public String getPathSummary() {
        return pathSummary;
    }
}
