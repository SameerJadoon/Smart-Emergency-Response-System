package com.elevate.emergency;

import org.junit.jupiter.api.Test;

/**
 * Integration-style tests for DispatchManager: verify the nearest idle
 * ambulance is chosen, and that calls queue correctly when the fleet
 * is fully busy.
 */
public class DispatchManagerTest {

    @Test
    void dispatchesNearestIdleAmbulance() {
        // TODO: build a small fleet + graph, submit a patient call,
        // assert the closest ambulance was selected and its status changed
    }

    @Test
    void queuesCallWhenFleetIsBusy() {
        // TODO: mark all ambulances busy, submit a call, assert it lands
        // in waitingForAmbulance instead of being dispatched
    }
}
