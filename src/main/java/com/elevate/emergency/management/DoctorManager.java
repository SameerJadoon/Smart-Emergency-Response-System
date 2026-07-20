package com.elevate.emergency.management;

import com.elevate.emergency.algorithms.RoundRobinScheduler;
import com.elevate.emergency.datastructures.DoublyLinkedList;
import com.elevate.emergency.model.Doctor;
import com.elevate.emergency.model.Patient;

/**
 * Manages the doctor roster and assigns patients using round-robin
 * scheduling among currently available doctors.
 */
public class DoctorManager {

    private DoublyLinkedList<Doctor> doctors;
    private RoundRobinScheduler scheduler;

    public DoctorManager(DoublyLinkedList<Doctor> doctors) {
        this.doctors = doctors;
        this.scheduler = new RoundRobinScheduler(doctors);
    }

    public Doctor assignDoctor(Patient patient) {
        // TODO: get next available doctor from scheduler, mark BUSY,
        // link doctor.currentPatientId = patient.getId()
        return null;
    }

    public void freeDoctor(Doctor doctor) {
        // TODO: mark AVAILABLE, clear currentPatientId
    }
}
