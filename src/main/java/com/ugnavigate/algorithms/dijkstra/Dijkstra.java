package com.ugnavigate.algorithms.dijkstra;

import com.ugnavigate.models.Edge;
import com.ugnavigate.models.GraphNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dijkstra {
    Map<GraphNode, Double> unvisited = new HashMap<>();
    Map<GraphNode, Double> visited = new HashMap<>();
    private GraphNode destination;

    public Dijkstra(GraphNode start, GraphNode destination){
        this.destination = destination;
        initilizeUnvisited(start);
    }

    public void initilizeUnvisited(GraphNode start){
        List<Edge> neighbors = start.getNeighbors();
        for (Edge neighbor: neighbors){
            unvisited.put(neighbor.getTo(), neighbor.getDistance());
        }
    }

    public double getShortestPath(GraphNode curNode, double curNodeDistance){
//        initialize visited to keep track of nodes whose shortest path have been found
//        initialize unvisited to keep track of nodes whose shortest path have not been found yet
//        from the unvisited, select the node with the shortest distance
//        from that node, getShortestPath to our destination
//        i.e form that node, we get the new distances to all the neighbors of that node that are not visited yet
//        if the new distance gives a shorter path, update it, else live it.
//        after visiting a node, remove it from the unvisited and add to visited

        if (curNode == destination){
            System.out.println(curNodeDistance);
            return curNodeDistance;
        }

        List<Edge> neighbors = curNode.getNeighbors();
        for (Edge edge: neighbors){
            GraphNode neighbor = edge.getTo();
            if (!visited.containsKey(neighbor)){
                double distance = curNodeDistance + edge.getDistance();
                unvisited.put(neighbor, Math.min(unvisited.getOrDefault(neighbor, distance), distance ));
            }
        }

        GraphNode smallest_node = null;
        Double smallest_distance = 10000000.0;
        for (Map.Entry<GraphNode, Double> unvisted_node: unvisited.entrySet()){
            GraphNode node = unvisted_node.getKey();
            Double node_distance = unvisted_node.getValue();

            if (node_distance < smallest_distance){
                smallest_distance = node_distance;
                smallest_node = node;
            }
        }


        if (smallest_node != null){
            unvisited.remove(smallest_node);
            visited.put(smallest_node, smallest_distance);
            getShortestPath(smallest_node, smallest_distance);
        }
//        track the path and return instead...
//        System.out.println(smallest_distance);
        return smallest_distance;
    }
}
