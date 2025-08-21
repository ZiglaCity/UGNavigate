package com.ugnavigate.algorithms.dijkstra;

import com.ugnavigate.models.Graph;
import com.ugnavigate.models.GraphNode;
import com.ugnavigate.models.Route;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Small adapter providing a static convenience method to find shortest path using existing Dijkstra class.
 */
public class DijkstraAdapter {

    public static Route findShortestPath(Graph graph, String startName, String destName) {
        if (graph == null || startName == null || destName == null) return null;

        GraphNode start = graph.getNode(startName);
        GraphNode dest = graph.getNode(destName);
        if (start == null || dest == null) return null;

        Dijkstra solver = new Dijkstra(start, dest);
        solver.solve();
        List<GraphNode> pathNodes = solver.getShortestPath();
        double distMeters = solver.getShortestDistance();

        List<String> pathNames = pathNodes == null ? List.of() : pathNodes.stream().map(GraphNode::getId).collect(Collectors.toList());
        double distKm = distMeters / 1000.0;
        double estMin = distKm * 12.0; // crude conversion: 12 min per km

        return new Route(pathNames, distKm, estMin);
    }
}
