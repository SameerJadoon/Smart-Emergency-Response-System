package com.elevate.emergency.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Weighted graph representing the city road network.
 * Nodes = intersections (identified by int id), edges = roads with weights
 * (distance or travel time). Built as an adjacency list.
 *
 * Used by algorithms.Dijkstra and algorithms.BFS.
 */
public class Graph {

    /** Lightweight edge record used inside the adjacency list. */
    public static class Edge {
        public final int toNodeId;
        public double weight;

        public Edge(int toNodeId, double weight) {
            this.toNodeId = toNodeId;
            this.weight = weight;
        }
    }

    private final int numNodes;
    private final List<List<Edge>> adjacencyList;
    private final boolean directed;

    public Graph(int numNodes, boolean directed) {
        this.numNodes = numNodes;
        this.directed = directed;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int fromNodeId, int toNodeId, double weight) {
        adjacencyList.get(fromNodeId).add(new Edge(toNodeId, weight));
        if (!directed) {
            adjacencyList.get(toNodeId).add(new Edge(fromNodeId, weight));
        }
    }

    public List<Edge> getNeighbors(int nodeId) {
        return adjacencyList.get(nodeId);
    }

    public int getNumNodes() { return numNodes; }

    public double getEdgeWeight(int fromNodeId, int toNodeId) {
        for (Edge e : adjacencyList.get(fromNodeId)) {
            if (e.toNodeId == toNodeId) return e.weight;
        }
        return -1;
    }

    /** Updates the weight of an edge in both directions (if undirected). Used by TrafficUpdater. */
    public void updateEdgeWeight(int fromNodeId, int toNodeId, double newWeight) {
        for (Edge e : adjacencyList.get(fromNodeId)) {
            if (e.toNodeId == toNodeId) e.weight = newWeight;
        }
        if (!directed) {
            for (Edge e : adjacencyList.get(toNodeId)) {
                if (e.toNodeId == fromNodeId) e.weight = newWeight;
            }
        }
    }
}
