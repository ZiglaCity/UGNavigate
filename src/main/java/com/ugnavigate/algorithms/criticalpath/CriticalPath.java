package com.ugnavigate.algorithms.criticalpath;

import com.ugnavigate.algorithms.floydwarshall.FloydWarshall;
import com.ugnavigate.models.Graph;
import com.ugnavigate.models.GraphNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Critical path defined here as the longest shortest-path between any two nodes (graph diameter).
 */
public class CriticalPath {

	public static List<String> findCriticalPath(Graph graph) {
		List<String> out = new ArrayList<>();
		if (graph == null) return out;

		Map<String, Map<String, Double>> all = FloydWarshall.computeAllPairs(graph);

		String bestU = null, bestV = null;
		double bestDist = -1.0;

		for (Map.Entry<String, Map<String, Double>> e : all.entrySet()) {
			String u = e.getKey();
			for (Map.Entry<String, Double> f : e.getValue().entrySet()) {
				String v = f.getKey();
				double d = f.getValue();
				if (d != Double.POSITIVE_INFINITY && d > bestDist) {
					bestDist = d;
					bestU = u;
					bestV = v;
				}
			}
		}

		if (bestU != null && bestV != null) {
			out.addAll(FloydWarshall.reconstructPath(bestU, bestV));
		}

		return out;
	}
}
