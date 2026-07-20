package com.elevate.emergency.simulation;

import com.elevate.emergency.management.DispatchManager;
import com.elevate.emergency.management.HospitalManager;

/**
 * Drives the simulation forward in discrete time steps ("ticks").
 * Each tick: generates possible new emergencies, advances ambulances
 * along their routes, and lets the hospital process waiting patients.
 */
public class SimulationClock {

    private long currentTime;
    private final long tickIntervalMillis; // simulated minutes per tick, e.g.
    private DispatchManager dispatchManager;
    private HospitalManager hospitalManager;
    private EventGenerator eventGenerator;
    private TrafficUpdater trafficUpdater;

    public SimulationClock(DispatchManager dispatchManager, HospitalManager hospitalManager,
                            EventGenerator eventGenerator, TrafficUpdater trafficUpdater) {
        this.dispatchManager = dispatchManager;
        this.hospitalManager = hospitalManager;
        this.eventGenerator = eventGenerator;
        this.trafficUpdater = trafficUpdater;
        this.currentTime = 0;
        this.tickIntervalMillis = 1;
    }

    /** Runs the simulation for the given number of ticks. */
    public void run(int totalTicks) {
        for (int t = 0; t < totalTicks; t++) {
            tick();
        }
    }

    private void tick() {
        currentTime++;
        // TODO:
        // 1. eventGenerator.maybeGenerateEmergency(currentTime) -> dispatchManager.receiveEmergencyCall()
        // 2. trafficUpdater.maybeUpdateTraffic()
        // 3. advance ambulances currently en route (check if they've arrived)
        // 4. hospitalManager.processNextAvailablePatient()
    }

    public long getCurrentTime() {
        return currentTime;
    }
}
