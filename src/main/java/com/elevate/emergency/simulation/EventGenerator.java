package com.elevate.emergency.simulation;

import com.elevate.emergency.model.Patient;

import java.util.Random;

/**
 * Randomly generates emergency calls following a Poisson-like arrival
 * pattern, each with a random location and urgency level.
 */
public class EventGenerator {

    private final Random random = new Random();
    private final double arrivalRatePerTick; // lambda for Poisson process
    private int nextPatientId = 1;

    public EventGenerator(double arrivalRatePerTick) {
        this.arrivalRatePerTick = arrivalRatePerTick;
    }

    /**
     * Returns a newly generated Patient for this tick, or null if no
     * emergency occurred this tick.
     */
    public Patient maybeGenerateEmergency(long currentTime, int numGraphNodes) {
        // TODO:
        // 1. Sample whether an event occurs this tick (Poisson approx via random.nextDouble() < arrivalRatePerTick)
        // 2. If yes, pick random locationNodeId in [0, numGraphNodes)
        // 3. Pick random urgencyLevel 1-5 (weight toward less severe for realism)
        // 4. Build and return new Patient
        return null;
    }
}
