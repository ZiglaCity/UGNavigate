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
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            throw new RuntimeException("Error loading graph: " + e.getMessage(), e);
        }

//        return adjacencyList;
    }
}
