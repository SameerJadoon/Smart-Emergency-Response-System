package com.elevate.emergency.simulation;

import com.elevate.emergency.datastructures.Graph;

import java.util.Random;

/**
 * Periodically perturbs edge weights in the city Graph to simulate
 * traffic congestion, forcing RoutePlanner to recompute routes.
 */
public class TrafficUpdater {

    private final Graph cityGraph;
    private final Random random = new Random();
    private final double updateProbabilityPerTick;

    public TrafficUpdater(Graph cityGraph, double updateProbabilityPerTick) {
        this.cityGraph = cityGraph;
        this.updateProbabilityPerTick = updateProbabilityPerTick;
    }

    /** Randomly increases/decreases a few edge weights to simulate congestion clearing or building up. */
    public void maybeUpdateTraffic() {
        // TODO: with probability updateProbabilityPerTick, pick random edge(s)
        // and multiply weight by a random congestion factor (e.g. 0.8x - 2x)
    }
}
