package com.ugnavigate.algorithms.dijkstra;

import com.ugnavigate.models.Edge;
import com.ugnavigate.models.GraphNode;

import java.util.ArrayList;
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
    private Map<GraphNode, GraphNode> to_fromPathBuilder;
    private String pathSummary;

    public Dijkstra(GraphNode start, GraphNode destination){
        this.destination = destination;
        this.start = start;
        this.to_fromPathBuilder = new HashMap<>();
        this.shortestPath = new ArrayList<>();
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
            visited.put(start, 0.0);
            for (Edge edge: start.getNeighbors()){
                to_fromPathBuilder.put(edge.getTo(), start);
            }
            Solution(start, startDistance);
        }
        pathBuilder();
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
            if(!to_fromPathBuilder.containsKey(smallest_node)){
                to_fromPathBuilder.put(smallest_node, curNode);
            }
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

    public void pathBuilder(){
        GraphNode cur = destination;
        GraphNode from = to_fromPathBuilder.get(cur);
        shortestPath.add(cur);
        String dummyPath = cur.getId() + " --> ";
        System.out.println(shortestPath);
        String startName = start.getId();
        while(from != start){
            GraphNode temp = cur;
            cur = from;
            String curName = cur.getId();
            from = to_fromPathBuilder.get(temp);
            String fromName = from.getId();
            dummyPath += curName + " <-- ";
            shortestPath.add(cur);
        }

        pathSummary = "From " + start.getLandmark().getName() + " ";
        for(GraphNode node: shortestPath){
            pathSummary += " Go to " + node.getLandmark().getName();
        }
    }

    public String getPathSummary() {
        return pathSummary;
    }
}
