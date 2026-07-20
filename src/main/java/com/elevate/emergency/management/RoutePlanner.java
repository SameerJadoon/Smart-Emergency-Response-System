package com.elevate.emergency.management;

import com.elevate.emergency.algorithms.Dijkstra;
import com.elevate.emergency.datastructures.Graph;

/**
 * Thin wrapper around Dijkstra's algorithm for the two routes this
 * project needs: ambulance -> patient, and patient -> hospital.
 */
public class RoutePlanner {

    private final Graph cityGraph;

    public RoutePlanner(Graph cityGraph) {
        this.cityGraph = cityGraph;
    }

    /** Returns the sequence of node ids forming the shortest route. */
    public int[] planRoute(int fromNodeId, int toNodeId) {
        // TODO: double[] distances = Dijkstra.shortestPaths(cityGraph, fromNodeId);
        //       int[] previous = ... (need Dijkstra to also return previous[])
        //       return Dijkstra.reconstructPath(previous, toNodeId);
        return new int[0];
    }

    public double routeDistance(int fromNodeId, int toNodeId) {
        // TODO: return distances[toNodeId] from Dijkstra.shortestPaths
        return -1;
    }
}
