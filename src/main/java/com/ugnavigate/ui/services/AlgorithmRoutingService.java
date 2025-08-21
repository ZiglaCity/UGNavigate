package com.ugnavigate.ui.services;

import com.ugnavigate.algorithms.dijkstra.DijkstraAdapter;
import com.ugnavigate.models.Graph;
import com.ugnavigate.models.Route;
import com.ugnavigate.ui.models.RouteRequest;
import com.ugnavigate.ui.models.RouteResult;

import java.util.ArrayList;
import java.util.List;

/**
 * RoutingService implementation that uses the algorithmic backend (DijkstraAdapter).
 */
public class AlgorithmRoutingService implements RoutingService {

    private final Graph graph;

    public AlgorithmRoutingService(Graph graph) {
        this.graph = graph;
    }

    @Override
    public List<RouteResult> calculateRoutes(RouteRequest request) {
        if (request == null) return List.of();
        List<RouteResult> results = new ArrayList<>();

        // For now, return a single route computed by DijkstraAdapter
        Route r = DijkstraAdapter.findShortestPath(graph, request.getStart(), request.getDestination());
        if (r == null) return results;

        results.add(new RouteResult(1, r.getPath(), r.getDistanceKm(), r.getEstimatedTimeMin(), List.of()));
        return results;
    }
}
