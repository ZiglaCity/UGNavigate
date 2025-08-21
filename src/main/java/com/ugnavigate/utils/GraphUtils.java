package com.ugnavigate.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ugnavigate.models.Neighbor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.stream.Collectors;
import com.ugnavigate.models.Graph;
import com.ugnavigate.models.GraphNode;


public class GraphUtils {
    /**
     * Loads an adjacency list with coordinates and directions from JSON.
     * @param filePath Path to adjacency_list.json
     * @return Map of node name -> list of neighbor info
     */
    public static Map<String, List<Neighbor>> loadGraph(String filePath) {
        Map<String, List<Neighbor>> adjacencyList = new HashMap<>();

        try {
            InputStream inputStream = GraphUtils.class.getClassLoader()
                    .getResourceAsStream(filePath);

            if (inputStream == null) {
                throw new RuntimeException("File not found in resources: " + filePath);
            }

            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<Neighbor>>>(){}.getType();
            adjacencyList = gson.fromJson(reader, type);
            return adjacencyList;
        } catch (Exception e) {
            throw new RuntimeException("Error loading graph: " + e.getMessage(), e);
        }
    }

    /**
     * Return list of landmark/node name suggestions that start with the given prefix (case-insensitive).
     * This helper expects a constructed Graph (not the raw adjacency map).
     */
    public static List<String> getSuggestions(Graph g, String prefix) {
        if (g == null || prefix == null || prefix.isBlank()) return Collections.emptyList();
        String p = prefix.toLowerCase();
        return g.getAllNodes().stream()
                .map(GraphNode::getId)
                .filter(name -> name != null && name.toLowerCase().startsWith(p))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Verify that a location name exists in the graph (case-insensitive).
     */
    public static boolean verifyLocation(Graph g, String name) {
        if (g == null || name == null || name.isBlank()) return false;
        String target = name.trim().toLowerCase();
        return g.getAllNodes().stream()
                .map(GraphNode::getId)
                .filter(n -> n != null)
                .anyMatch(n -> n.trim().toLowerCase().equals(target));
    }
}
