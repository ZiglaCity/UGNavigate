package com.ugnavigate.ui;

import com.ugnavigate.algorithms.criticalpath.CriticalPath;
import com.ugnavigate.models.Graph;
import com.ugnavigate.utils.GraphUtils;
import com.ugnavigate.ui.models.RouteRequest;
import com.ugnavigate.ui.models.RouteResult;
import com.ugnavigate.ui.services.AlgorithmRoutingService;
import com.ugnavigate.ui.services.RoutingService;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleUI {

	private final RoutingService routingService = null; // will be created per-run when graph is available

	public void run(Graph graph) {
		if (graph == null) throw new IllegalArgumentException("Graph cannot be null");
		// use algorithm-backed routing service
		RoutingService routingService = new AlgorithmRoutingService(graph);

		System.out.println("UGNavigate console demo");
		try (Scanner sc = new Scanner(System.in)) {
			System.out.print("Start: ");
			String start = sc.nextLine().trim();
			System.out.print("Destination: ");
			String dest = sc.nextLine().trim();

			// suggestions demo
			List<String> suggestions = GraphUtils.getSuggestions(graph, start);
			if (!suggestions.isEmpty()) {
				System.out.println("Suggestions for start: " + suggestions.stream().limit(5).collect(Collectors.joining(", ")));
			}

			RouteRequest req = new RouteRequest(start, dest, null, "distance");
			List<RouteResult> results = routingService.calculateRoutes(req);

			if (results.isEmpty()) {
				System.out.println("No available route found.");
				return;
			}

			System.out.println("Found routes:");
			for (RouteResult r : results) {
				System.out.println(String.format("Route %d: %s — %.2f km, %.1f min", r.getRouteId(), String.join(" -> ", r.getPath()), r.getDistanceKm(), r.getEstimatedTimeMin()));
			}

			// ask user if they'd like to render the found route
			System.out.print("Render this route on map? (y/N): ");
			String render = sc.nextLine().trim();
			if (render.equalsIgnoreCase("y") && !results.isEmpty()) {
				MapRenderer.render(graph, results.get(0).getPath());
			}

			// offer critical path visualization
			System.out.print("Show critical path for the graph? (y/N): ");
			String cp = sc.nextLine().trim();
			if (cp.equalsIgnoreCase("y")) {
				List<String> critical = CriticalPath.findCriticalPath(graph);
				if (critical.isEmpty()) System.out.println("No critical path found.");
				else MapRenderer.render(graph, critical);
			}
		}
	}

}
