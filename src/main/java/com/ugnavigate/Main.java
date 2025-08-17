package com.ugnavigate;

import com.ugnavigate.models.*;
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
        System.out.println(landmarks);

        Graph graph = new Graph(landmarks, adjacencyList);

        GraphNode start = graph.getNode("Ecobank");
        System.out.println("Diaspora Node: " + start.getId());
        System.out.println("Landmark Details: " + start.getLandmark());
        System.out.println("Tags related to landmark: " + start.getLandmark().getTags());

//        checking to see if Ecobank can be identified as a bank...
        Map<String, String> tagsOfStart = start.getLandmark().getTags();
        if (tagsOfStart.containsKey("bank") || tagsOfStart.containsValue("bank")) {
            System.out.println("This landmark is a bank!");
        } else {
            System.out.println(" Not a bank.");
        }

        System.out.println("Neighbors:");
        for (Edge edge : start.getNeighbors()) {
            GraphNode neighbor = edge.getTo();
            double weight = edge.getWeight();
            System.out.println(" - " + neighbor.getId() + " (" + neighbor.getLandmark().getName() + ") at a distance: " + edge.getDistance() + "M and direction: " + edge.getDirection()  );
        }
    }
}
