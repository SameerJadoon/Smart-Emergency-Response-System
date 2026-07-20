package com.elevate.emergency.model;

/**
 * Represents an ambulance unit in the fleet.
 */
public class Ambulance {

    public enum Status { IDLE, TO_PATIENT, TO_HOSPITAL }

    private final String id;
    private int currentNodeId;
    private Status status;
    private String assignedPatientId;

    public Ambulance(String id, int currentNodeId) {
        this.id = id;
        this.currentNodeId = currentNodeId;
        this.status = Status.IDLE;
    }

    public String getId() { return id; }

    public int getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(int currentNodeId) { this.currentNodeId = currentNodeId; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAssignedPatientId() { return assignedPatientId; }
    public void setAssignedPatientId(String assignedPatientId) { this.assignedPatientId = assignedPatientId; }
}
