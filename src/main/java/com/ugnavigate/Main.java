package com.ugnavigate;

import com.ugnavigate.algorithms.criticalpath.CriticalPath;
import com.ugnavigate.algorithms.dijkstra.Dijkstra;
import com.ugnavigate.models.*;
import com.ugnavigate.ui.MapRenderer;
import com.ugnavigate.utils.GraphUtils;
import com.ugnavigate.utils.LandmarkLoader;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String filePath = "data/adjacency_list.json";
        Map<String, List<Neighbor>> adjacencyList = GraphUtils.loadGraph(filePath);
//        System.out.println("=== UG Navigate Graph ===");
//        for (Map.Entry<String, List<Neighbor>> entry : adjacencyList.entrySet()) {
//            System.out.println(entry.getKey() + " ->");
//            for (Neighbor neighbor : entry.getValue()) {
//                System.out.println("    " + neighbor);
//            }
//        }

        filePath = "data/landmarks.json";
        Map<String, Landmark> landmarks = LandmarkLoader.loadLandmarks(filePath);
    System.out.println("Loaded landmark keys: " + landmarks.keySet());

        Graph graph = new Graph(landmarks, adjacencyList);

        GraphNode start = graph.getNode("JQB");
        if (start == null) {
            System.out.println("Start node 'JQB' not found in graph.");
        } else {
            System.out.println("JQB Node: " + start.getId());
            Landmark sLm = start.getLandmark();
            System.out.println("Landmark Details: " + sLm);
            System.out.println("Tags related to landmark: " + (sLm != null ? sLm.getTags() : "null"));

            // checking to see if this landmark can be identified as a bank...
            Map<String, String> tagsOfStart = (sLm != null && sLm.getTags() != null) ? sLm.getTags() : java.util.Collections.emptyMap();
            if (tagsOfStart.containsKey("bank") || tagsOfStart.containsValue("bank")) {
                System.out.println("This landmark is a bank!");
            } else {
                System.out.println(" Not a bank.");
            }
        }

        System.out.println("Neighbors:");
        for (Edge edge : start.getNeighbors()) {
            GraphNode neighbor = edge.getTo();
            String neighborName = (neighbor.getLandmark() != null) ? neighbor.getLandmark().getName() : "Unknown";
            System.out.println(" - " + neighbor.getId() + " (" + neighborName + ") at a distance: " + edge.getDistance() + "M and direction: " + edge.getDirection()  );
        }

//        start = graph.getNode("Computer Science Dept");
        GraphNode destination = graph.getNode("N Block");

        Dijkstra dj = new Dijkstra(start, destination);
//        dj.solveUsingDijkstra();
//        System.out.println(dj.getShortestDistance());

        start = graph.getNode("Balme Library");
        destination = graph.getNode("Central Cafeteria");

        if (!graph.getAllNodes().contains(start) || !graph.getAllNodes().contains(destination)) {
            throw new IllegalArgumentException("Invalid path provided! One or both nodes are not in the graph.");
        }

        dj = new Dijkstra(start, destination);
        dj.solve();
        System.out.println(dj.getShortestDistance());
        System.out.println(dj.getPathSummary());

        // Demonstrate critical path visualization
        System.out.println("\n=== Critical Path Analysis ===");
        List<String> criticalPath = CriticalPath.findCriticalPath(graph);
        if (!criticalPath.isEmpty()) {
            System.out.println("Critical path (longest shortest path): " + String.join(" -> ", criticalPath));
            MapRenderer.render(graph, criticalPath, "Critical Path - Graph Diameter");
        } else {
            System.out.println("No critical path found");
        }

        // Launch simple console UI demo
        com.ugnavigate.ui.ConsoleUI ui = new com.ugnavigate.ui.ConsoleUI();
        ui.run(graph);

    }
}
