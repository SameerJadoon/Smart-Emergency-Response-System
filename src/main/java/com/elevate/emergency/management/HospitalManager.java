package com.elevate.emergency.management;

import com.elevate.emergency.model.Hospital;
import com.elevate.emergency.model.Patient;

/**
 * Top-level coordinator for the single hospital instance: ties together
 * TriageManager and DoctorManager, and is the entry point the
 * SimulationClock calls whenever a patient arrives at the hospital.
 */
public class HospitalManager {

    private Hospital hospital;
    private TriageManager triageManager;
    private DoctorManager doctorManager;

    public HospitalManager(Hospital hospital, TriageManager triageManager, DoctorManager doctorManager) {
        this.hospital = hospital;
        this.triageManager = triageManager;
        this.doctorManager = doctorManager;
    }

    /** Called when an ambulance drops a patient off at the hospital. */
    public void handlePatientArrival(Patient patient) {
        // TODO: triageManager.admitPatient(patient), log timestamp
    }

    /** Called each simulation tick to try to move waiting patients to a doctor. */
    public void processNextAvailablePatient() {
        // TODO: if a doctor is free, triageManager.callNextPatient(),
        // doctorManager.assignDoctor(patient)
    }
}
