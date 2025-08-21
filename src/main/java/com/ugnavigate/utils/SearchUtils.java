package com.ugnavigate.utils;

import com.ugnavigate.models.Graph;
import com.ugnavigate.models.GraphNode;

/**
 * Small search helpers used by the UI.
 */
public class SearchUtils {

	/**
	 * Find nearest graph node to the provided coordinates using simple Euclidean distance.
	 */
	public static GraphNode findNearest(Graph graph, double lat, double lon) {
		if (graph == null) return null;
		GraphNode best = null;
		double bestDist = Double.POSITIVE_INFINITY;
		for (GraphNode node : graph.getAllNodes()) {
			if (node.getLandmark() == null) continue;
			double dlat = node.getLandmark().getLat() - lat;
			double dlon = node.getLandmark().getLon() - lon;
			double dist = dlat * dlat + dlon * dlon;
			if (dist < bestDist) {
				bestDist = dist;
				best = node;
			}
		}
		return best;
	}
}
