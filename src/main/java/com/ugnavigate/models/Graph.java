package com.ugnavigate.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final Map<String, GraphNode> nodes = new HashMap<>();

    public Graph(Map<String, Landmark> landmarks, Map<String, List<String>> adjacencyList) {
        for (String id : adjacencyList.keySet()) {
            GraphNode node = getOrCreateNode(id);
            node.setLandmark(landmarks.get(id));
        }

        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            GraphNode node = nodes.get(entry.getKey());
            for (String neighborId : entry.getValue()) {
                GraphNode neighbor = getOrCreateNode(neighborId);
                //  the weights of the edges will later be determined from the tags related to the landmarks...(eg; banks and markets: 3, shops: 2, rest: 1;
                Edge edge = new Edge(node, neighbor, 1);
                node.addNeighbor(edge);
            }
        }
    }

    private GraphNode getOrCreateNode(String id) {
        return nodes.computeIfAbsent(id, GraphNode::new);

//        another way to say;
//        if (map.get(key) == null) {     V newValue = mappingFunction.apply(key);     if (newValue != null)         map.put(key, newValue); }
    }

    public GraphNode getNode(String id) {
        return nodes.get(id);
    }

    public Collection<GraphNode> getAllNodes() {
        return nodes.values();
    }
}
