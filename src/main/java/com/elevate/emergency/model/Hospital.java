package com.elevate.emergency.model;

import com.elevate.emergency.datastructures.DoublyLinkedList;
import com.elevate.emergency.datastructures.MinHeap;

/**
 * Represents the single hospital in the system.
 * Holds the triage priority queue and the doctor fleet.
 * This system is intentionally bounded to ONE hospital instance.
 */
public class Hospital {

    private final String name;
    private final int locationNodeId;    // where hospital sits on the RoadGraph
    private final int capacity;

    private MinHeap<Patient> triageQueue;          // patients waiting, ordered by urgency
    private DoublyLinkedList<Doctor> doctors;       // doctor fleet

    public Hospital(String name, int locationNodeId, int capacity) {
        this.name = name;
        this.locationNodeId = locationNodeId;
        this.capacity = capacity;
        // TODO: initialize triageQueue and doctors
    }

    // TODO: admitPatient(Patient p), getNextPatient(), isFull()

    public int getLocationNodeId() { return locationNodeId; }
}
