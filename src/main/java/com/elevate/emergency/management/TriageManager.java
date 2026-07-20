package com.elevate.emergency.management;

import com.elevate.emergency.datastructures.LinkedQueue;
import com.elevate.emergency.datastructures.MinHeap;
import com.elevate.emergency.model.Patient;

/**
 * Manages the hospital's triage priority queue. Patients with the same
 * urgency level are served in arrival order via the secondary FIFO queue.
 */
public class TriageManager {

    private MinHeap<Patient> triageHeap;              // primary ordering: urgency
    private LinkedQueue<Patient> sameUrgencyQueue;      // secondary ordering: arrival time

    public TriageManager() {
        // TODO: initialize triageHeap with comparator on urgencyLevel
    }

    public void admitPatient(Patient patient) {
        // TODO: insert into triageHeap
    }

    public Patient callNextPatient() {
        // TODO: extractMin from triageHeap
        return null;
    }

    public boolean isEmpty() {
        // TODO
        return true;
    }
}
