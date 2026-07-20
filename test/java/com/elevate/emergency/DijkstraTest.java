package com.elevate.emergency;

import com.elevate.emergency.algorithms.Dijkstra;
import com.elevate.emergency.datastructures.Graph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Dijkstra's algorithm on a small known graph
 * (e.g. matching resources/city_map.json) with hand-verifiable distances.
 */
public class DijkstraTest {

    @Test
    void computesCorrectShortestDistance() {
        Graph g = new Graph(4, false);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(0, 2, 5);
        g.addEdge(2, 3, 1);

        double[] distances = Dijkstra.shortestPaths(g, 0);
        // TODO: once shortestPaths() is implemented, assert:
        // distances[0] == 0, distances[1] == 1, distances[2] == 3, distances[3] == 4
    }
}
