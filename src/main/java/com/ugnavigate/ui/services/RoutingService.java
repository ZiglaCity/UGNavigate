package com.ugnavigate.ui.services;

import com.ugnavigate.ui.models.RouteRequest;
import com.ugnavigate.ui.models.RouteResult;

import java.util.List;

public interface RoutingService {
    List<RouteResult> calculateRoutes(RouteRequest request);
}
