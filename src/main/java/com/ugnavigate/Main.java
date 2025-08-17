package com.ugnavigate;

import com.ugnavigate.utils.GraphUtils;
import com.ugnavigate.models.Neighbor;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String filePath = "data/adjacency_list.json";
        Map<String, List<Neighbor>> graph = GraphUtils.loadGraph(filePath);

        System.out.println("=== UG Navigate Graph ===");
        for (Map.Entry<String, List<Neighbor>> entry : graph.entrySet()) {
            System.out.println(entry.getKey() + " ->");
            for (Neighbor neighbor : entry.getValue()) {
                System.out.println("    " + neighbor);
            }
        }
    }
}
