package com.ugnavigate.algorithms.dijkstra;

import com.ugnavigate.models.Graph;
import com.ugnavigate.models.Landmark;
import com.ugnavigate.utils.GraphUtils;
import com.ugnavigate.utils.LandmarkLoader;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraAdapterTest {

    @Test
    public void basicShortestPath() {
        Map<String, List<com.ugnavigate.models.Neighbor>> adj = GraphUtils.loadGraph("data/adjacency_list.json");
        Map<String, Landmark> landmarks = LandmarkLoader.loadLandmarks("data/landmarks.json");
        Graph graph = new Graph(landmarks, adj);

        var route = DijkstraAdapter.findShortestPath(graph, "Balme Library", "N Block");
        assertNotNull(route);
        assertTrue(route.getPath().size() >= 2);
    }
}
