package com.ugnavigate.algorithms.dijkstra;

import com.ugnavigate.models.Edge;
import com.ugnavigate.models.GraphNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dijkstra {
    Map<GraphNode, Double> unvisited = new HashMap<>();
    Map<GraphNode, Double> visited = new HashMap<>();
    private final GraphNode destination;
    private double shortestDistance;
    private final GraphNode start;
    private List<GraphNode> shortestPath;

    public Dijkstra(GraphNode start, GraphNode destination){
        this.destination = destination;
        this.start = start;
        initializeUnvisited(start);
    }

    public void initializeUnvisited(GraphNode start){
        List<Edge> neighbors = start.getNeighbors();
        for (Edge neighbor: neighbors){
            unvisited.put(neighbor.getTo(), neighbor.getDistance());
        }
    }

    public double getShortestDistance(){
        return shortestDistance;
    }

    public void solveUsingDijkstra(){
        if (this.start == destination){
            shortestDistance = 0;
            shortestPath = null;
        }
        else{
            double startDistance = 0.0;
            Solution(start, startDistance);
        }
    }

    public void Solution(GraphNode curNode, double curNodeDistance){
        //        initialize visited to keep track of nodes whose shortest path have been found
//        initialize unvisited to keep track of nodes whose shortest path have not been found yet
//        from the unvisited, select the node with the shortest distance
//        from that node, getShortestPath to our destination
//        i.e form that node, we get the new distances to all the neighbors of that node that are not visited yet
//        if the new distance gives a shorter path, update it, else live it.
//        after visiting a node, remove it from the unvisited and add to visited

        if (curNode == destination){
            System.out.println(curNodeDistance);
            shortestDistance = curNodeDistance;
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
            if (smallest_node != destination){
                Solution(smallest_node, smallest_distance);
            }
            else{
                shortestDistance = smallest_distance;
            }
        }
//        track the path and determine the shortest path...
    }

    public List<GraphNode> getShortestPath(){
        return shortestPath;
    }
}
