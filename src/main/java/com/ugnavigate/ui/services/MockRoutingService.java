package com.ugnavigate.ui.services;

import com.ugnavigate.ui.models.RouteRequest;
import com.ugnavigate.ui.models.RouteResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MockRoutingService implements RoutingService {

    @Override
    public List<RouteResult> calculateRoutes(RouteRequest request) {
        if (request == null || request.getStart() == null || request.getDestination() == null) {
            return Collections.emptyList();
        }

        RouteResult r1 = new RouteResult(
                1,
                Arrays.asList(request.getStart(), "JQB", "NNB", request.getDestination()),
                1.2,
                10.0,
                Arrays.asList("JQB")
        );

        RouteResult r2 = new RouteResult(
                2,
                Arrays.asList(request.getStart(), "Central Cafeteria", "NNB", request.getDestination()),
                1.5,
                12.0,
                Arrays.asList("Central Cafeteria")
        );

        RouteResult r3 = new RouteResult(
                3,
                Arrays.asList(request.getStart(), "Balme Library", "N Block", request.getDestination()),
                1.8,
                14.0,
                Arrays.asList("Balme Library")
        );

        return Arrays.asList(r1, r2, r3);
    }
}
