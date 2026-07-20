package com.elevate.emergency.algorithms;

import com.elevate.emergency.datastructures.DoublyLinkedList;
import com.elevate.emergency.model.Doctor;

/**
 * Assigns patients to doctors in round-robin fashion among available
 * doctors. Mirrors CPU round-robin scheduling concepts (each doctor gets
 * a "turn" before cycling back to the first).
 */
public class RoundRobinScheduler {

    private DoublyLinkedList<Doctor> doctors;
    // TODO: pointer/index tracking "whose turn is next"

    public RoundRobinScheduler(DoublyLinkedList<Doctor> doctors) {
        this.doctors = doctors;
    }

    /**
     * Returns the next available doctor in round-robin order, or null
     * if none are free.
     */
    public Doctor getNextAvailableDoctor() {
        // TODO: starting from the tracked pointer, walk the list looking
        // for the next AVAILABLE doctor, wrapping around if needed
        return null;
    }
}
