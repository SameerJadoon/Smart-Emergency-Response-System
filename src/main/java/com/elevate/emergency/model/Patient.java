package com.elevate.emergency.model;

/**
 * Represents a patient reported in an emergency call.
 */
public class Patient {

    private final String id;
    private String name;
    private int locationNodeId;
    private final int urgencyLevel;    // 1 = critical ... 5 = minor
    private final long callTimestamp;
    private long pickupTimestamp = -1;
    private long hospitalArrivalTimestamp = -1;
    private long dischargeTimestamp = -1;
    private boolean claimed = false; // true once assigned to a doctor (manually or automatically)

    public Patient(String id, String name, int locationNodeId, int urgencyLevel, long callTimestamp) {
        this.id = id;
        this.name = name;
        this.locationNodeId = locationNodeId;
        this.urgencyLevel = urgencyLevel;
        this.callTimestamp = callTimestamp;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getLocationNodeId() { return locationNodeId; }
    public int getUrgencyLevel() { return urgencyLevel; }
    public long getCallTimestamp() { return callTimestamp; }

    public long getPickupTimestamp() { return pickupTimestamp; }
    public void setPickupTimestamp(long t) { this.pickupTimestamp = t; }

    public long getHospitalArrivalTimestamp() { return hospitalArrivalTimestamp; }
    public void setHospitalArrivalTimestamp(long t) { this.hospitalArrivalTimestamp = t; }

    public long getDischargeTimestamp() { return dischargeTimestamp; }
    public void setDischargeTimestamp(long t) { this.dischargeTimestamp = t; }

    public long getResponseTime() {
        return hospitalArrivalTimestamp < 0 ? -1 : hospitalArrivalTimestamp - callTimestamp;
    }

    public String urgencyLabel() {
        switch (urgencyLevel) {
            case 1: return "Critical";
            case 2: return "Severe";
            case 3: return "Moderate";
            case 4: return "Mild";
            default: return "Minor";
        }
    }
}
