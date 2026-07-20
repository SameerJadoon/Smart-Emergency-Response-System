package com.elevate.emergency;

import com.elevate.emergency.datastructures.DoublyLinkedList;
import com.elevate.emergency.datastructures.Graph;
import com.elevate.emergency.management.*;
import com.elevate.emergency.model.*;
import com.elevate.emergency.simulation.*;
import com.elevate.emergency.util.Constants;

/**
 * Entry point. Wires up the city graph, hospital, fleet, and doctors,
 * then runs the simulation for a fixed number of ticks.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Smart Emergency Response & Hospital Management System ===");

        // TODO 1: Build the city road Graph (either hardcode a small sample
        // network here, or load resources/city_map.json)

        // TODO 2: Create the single Hospital instance

        // TODO 3: Create the ambulance fleet (DoublyLinkedList<Ambulance>)
        //          and doctor roster (DoublyLinkedList<Doctor>)

        // TODO 4: Wire up RoutePlanner, DispatchManager, TriageManager,
        //          DoctorManager, HospitalManager

        // TODO 5: Create EventGenerator, TrafficUpdater, SimulationClock

        // TODO 6: clock.run(Constants.TOTAL_SIMULATION_TICKS)

        // TODO 7: Print final StatsTracker summary report

        System.out.println("Simulation complete.");
    }
}
