package com.elevate.emergency.algorithms;

import com.elevate.emergency.datastructures.Graph;
import com.elevate.emergency.datastructures.LinkedQueue;

/**
 * Breadth-first search over the city RoadGraph.
 * Used for reachability queries, e.g. "which ambulances are within
 * N road-hops of this patient's location?"
 *
 * Complexity: O(V + E)
 */
public class BFS {

    public static boolean[] reachableWithinHops(Graph graph, int sourceNodeId, int maxHops) {
        int n = graph.getNumNodes();
        boolean[] visited = new boolean[n];
        int[] hopCount = new int[n];
        LinkedQueue<Integer> queue = new LinkedQueue<>();

        queue.enqueue(sourceNodeId);
        visited[sourceNodeId] = true;
        hopCount[sourceNodeId] = 0;

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            if (hopCount[current] >= maxHops) continue;
            for (Graph.Edge edge : graph.getNeighbors(current)) {
                if (!visited[edge.toNodeId]) {
                    visited[edge.toNodeId] = true;
                    hopCount[edge.toNodeId] = hopCount[current] + 1;
                    queue.enqueue(edge.toNodeId);
                }
            }
        }
        return visited;
    }
}
