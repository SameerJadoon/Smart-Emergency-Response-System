package com.elevate.emergency.model;

/**
 * Represents a doctor working at the hospital.
 */
public class Doctor {

    public enum Status { AVAILABLE, BUSY }

    private final String id;
    private final String name;
    private final String specialty;
    private Status status;
    private String currentPatientId;
    private long busyRemainingMs;

    public Doctor(String id, String name) {
        this(id, name, "General Medicine");
    }

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = (specialty == null || specialty.isBlank()) ? "General Medicine" : specialty;
        this.status = Status.AVAILABLE;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialty() { return specialty; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getCurrentPatientId() { return currentPatientId; }
    public void setCurrentPatientId(String currentPatientId) { this.currentPatientId = currentPatientId; }

    public long getBusyRemainingMs() { return busyRemainingMs; }
    public void setBusyRemainingMs(long busyRemainingMs) { this.busyRemainingMs = busyRemainingMs; }
}
