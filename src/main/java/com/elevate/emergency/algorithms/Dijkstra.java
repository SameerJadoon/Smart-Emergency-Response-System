package com.elevate.emergency.algorithms;

import com.elevate.emergency.datastructures.Graph;
import com.elevate.emergency.datastructures.MinHeap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Dijkstra's shortest path algorithm over the city RoadGraph.
 * Used by the RoutePlanner / SimController for:
 *   - ambulance -> patient location
 *   - patient location -> hospital
 *
 * Complexity: O((V + E) log V) using the custom MinHeap as the priority queue.
 */
public class Dijkstra {

    /** Pairing of a node id with its current best known distance from source. */
    private static class NodeDistance {
        final int nodeId;
        final double distance;
        NodeDistance(int nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }

    /** Result of a run: distances[] from source to every node, previous[] for path reconstruction. */
    public static class Result {
        public final double[] distances;
        public final int[] previous;
        public Result(double[] distances, int[] previous) {
            this.distances = distances;
            this.previous = previous;
        }
    }

    public static Result run(Graph graph, int sourceNodeId) {
        int n = graph.getNumNodes();
        double[] dist = new double[n];
        int[] previous = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(previous, -1);
        dist[sourceNodeId] = 0;

        MinHeap<NodeDistance> heap = new MinHeap<>(Comparator.comparingDouble(nd -> nd.distance));
        heap.insert(new NodeDistance(sourceNodeId, 0));

        while (!heap.isEmpty()) {
            NodeDistance current = heap.extractMin();
            if (visited[current.nodeId]) continue;
            visited[current.nodeId] = true;

            for (Graph.Edge edge : graph.getNeighbors(current.nodeId)) {
                if (visited[edge.toNodeId]) continue;
                double newDist = dist[current.nodeId] + edge.weight;
                if (newDist < dist[edge.toNodeId]) {
                    dist[edge.toNodeId] = newDist;
                    previous[edge.toNodeId] = current.nodeId;
                    heap.insert(new NodeDistance(edge.toNodeId, newDist));
                }
            }
        }
        return new Result(dist, previous);
    }

    /** Reconstructs the path (list of node ids, source -> target) using a Result's previous[] array. */
    public static int[] reconstructPath(Result result, int targetNodeId) {
        List<Integer> path = new ArrayList<>();
        int cur = targetNodeId;
        while (cur != -1) {
            path.add(0, cur);
            cur = result.previous[cur];
        }
        int[] arr = new int[path.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = path.get(i);
        return arr;
    }
}
