package com.elevate.emergency.logging;

/**
 * Aggregates performance statistics for the final report:
 * average response time, average wait time, average treatment time,
 * ambulance utilization, doctor utilization.
 */
public class StatsTracker {

    private long totalResponseTime;
    private long totalWaitTime;
    private int patientsServed;

    public void recordPatientCompletion(long responseTime, long waitTime) {
        // TODO: accumulate totals, increment patientsServed
    }

    public double getAverageResponseTime() {
        // TODO: return totalResponseTime / (double) patientsServed
        return 0;
    }

    public double getAverageWaitTime() {
        // TODO
        return 0;
    }

    // TODO: printSummaryReport()
}
