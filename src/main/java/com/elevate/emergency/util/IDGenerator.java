package com.elevate.emergency.util;

/**
 * Generates simple sequential/unique IDs for patients, ambulances, doctors.
 */
public class IDGenerator {

    private static int patientCounter = 0;
    private static int ambulanceCounter = 0;
    private static int doctorCounter = 0;

    public static String nextPatientId() {
        return "P-" + (++patientCounter);
    }

    public static String nextAmbulanceId() {
        return "A-" + (++ambulanceCounter);
    }

    public static String nextDoctorId() {
        return "D-" + (++doctorCounter);
    }
}
