package com.elevate.emergency.util;

/**
 * Shared configuration constants for the simulation.
 */
public class Constants {

    public static final int TOTAL_SIMULATION_TICKS = 500;
    public static final double EMERGENCY_ARRIVAL_RATE = 0.15; // events per tick
    public static final double TRAFFIC_UPDATE_PROBABILITY = 0.05;

    public static final int DEFAULT_AMBULANCE_FLEET_SIZE = 5;
    public static final int DEFAULT_DOCTOR_COUNT = 4;
    public static final int DEFAULT_HOSPITAL_CAPACITY = 20;

    public static final int MIN_URGENCY = 1; // most critical
    public static final int MAX_URGENCY = 5; // least critical

    private Constants() {} // prevent instantiation
}
