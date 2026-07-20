package com.elevate.emergency.gui;

import com.elevate.emergency.algorithms.Dijkstra;
import com.elevate.emergency.datastructures.Graph;
import com.elevate.emergency.datastructures.LinkedQueue;
import com.elevate.emergency.datastructures.MinHeap;
import com.elevate.emergency.model.Ambulance;
import com.elevate.emergency.model.Doctor;
import com.elevate.emergency.model.Patient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Drives the whole simulation for the GUI: owns the city Graph, the
 * ambulance fleet, the doctor roster, and the triage MinHeap.
 *
 * Supports both automatic operation (background emergencies, nearest-
 * ambulance dispatch, immediate doctor pickup) AND full manual control
 * (typed-in patient intake, multi-casualty incidents, click-to-dispatch a
 * specific ambulance, click-to-assign a specific doctor, add/remove
 * doctors). "Manual Control Mode" disables all automatic decision-making
 * so every dispatch and every doctor assignment must be done by staff
 * through the Manage tab.
 */
public class SimController {

    // ---- Node layout (normalized 0..1 coordinates for rendering) ----
    public static final double[] NODE_X = {0.10, 0.30, 0.30, 0.50, 0.55, 0.70, 0.82, 0.90};
    public static final double[] NODE_Y = {0.50, 0.16, 0.84, 0.16, 0.78, 0.46, 0.86, 0.46};
    public static final int HOSPITAL_NODE = 0;
    public static final int NUM_NODES = 8;

    /** Incident scale: bigger scale = more victims skew toward critical + more ambulances prioritized. */
    public enum IncidentScale {
        MINOR(1, "Minor"), MODERATE(2, "Moderate"), MAJOR(3, "Major"), MASS_CASUALTY(4, "Mass Casualty");
        public final int level;
        public final String label;
        IncidentScale(int level, String label) { this.level = level; this.label = label; }
    }

    /** One row of the final treatment report: who was treated, by whom, and when. */
    public static class TreatmentRecord {
        public final String patientId, patientName, urgencyLabel, doctorName, doctorSpecialty;
        public final long callTimestamp, hospitalArrivalTimestamp, dischargeTimestamp;
        public final double responseSeconds, treatmentSeconds;

        TreatmentRecord(Patient p, Doctor d, long treatmentStartMs, long dischargeMs) {
            this.patientId = p.getId();
            this.patientName = p.getName();
            this.urgencyLabel = p.urgencyLabel();
            this.doctorName = d.getName();
            this.doctorSpecialty = d.getSpecialty();
            this.callTimestamp = p.getCallTimestamp();
            this.hospitalArrivalTimestamp = p.getHospitalArrivalTimestamp();
            this.dischargeTimestamp = dischargeMs;
            this.responseSeconds = (p.getHospitalArrivalTimestamp() - p.getCallTimestamp()) / 1000.0;
            this.treatmentSeconds = (dischargeMs - treatmentStartMs) / 1000.0;
        }
    }

    private final Graph graph;
    private final List<Ambulance> fleet = new ArrayList<>();
    private final List<Doctor> doctors = new ArrayList<>();
    private final MinHeap<Patient> triageQueue;
    private final LinkedQueue<Patient> pendingCalls = new LinkedQueue<>();

    // Lazy-deletion set: patient ids that were manually pulled from the
    // waiting room directly, so a stale copy sitting in the MinHeap is
    // discarded instead of being treated twice.
    private final Set<String> alreadyHandledPatientIds = new HashSet<>();

    // Per-ambulance animation state, indexed matching `fleet`
    private final List<int[]> ambulanceRoutes = new ArrayList<>();
    private final List<Integer> ambulanceRouteIndex = new ArrayList<>();
    private final List<Double> ambulanceSegmentT = new ArrayList<>();
    private final Map<String, Patient> patientByAmbulance = new HashMap<>();
    private final Map<String, Patient> patientByDoctor = new HashMap<>();
    private final Map<String, Long> treatmentStartByDoctor = new HashMap<>();

    private final List<String> eventLog = new LinkedList<>();
    private final List<Patient> waitingRoom = new ArrayList<>();
    private final List<Long> completedResponseTimes = new ArrayList<>();
    private final List<TreatmentRecord> treatmentHistory = new ArrayList<>();

    private long simTimeMs = 0;
    private int patientCounter = 0;
    private int doctorCounter = 0;
    private int incidentCounter = 0;
    private final Random rng = new Random();

    private static final double MS_PER_WEIGHT_UNIT = 550;
    private double emergenciesPerSecond = 0.12;
    private boolean manualControlMode = false;

    public SimController() {
        graph = buildCityGraph();
        triageQueue = new MinHeap<>(Comparator
                .comparingInt(Patient::getUrgencyLevel)
                .thenComparingLong(Patient::getCallTimestamp));

        int[] stations = {1, 3, 4, 6};
        for (int i = 0; i < stations.length; i++) {
            Ambulance a = new Ambulance("A" + (i + 1), stations[i]);
            fleet.add(a);
            ambulanceRoutes.add(null);
            ambulanceRouteIndex.add(0);
            ambulanceSegmentT.add(0.0);
        }

        addDoctor("Dr. Ahmed", "Trauma");
        addDoctor("Dr. Sara", "General Medicine");
        addDoctor("Dr. Bilal", "Cardiology");

        log("Simulation initialized: " + fleet.size() + " ambulances, " + doctors.size() + " doctors on duty.");
    }

    private static Graph buildCityGraph() {
        Graph g = new Graph(NUM_NODES, false);
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 7);
        g.addEdge(1, 3, 3);
        g.addEdge(2, 3, 2);
        g.addEdge(2, 4, 6);
        g.addEdge(3, 5, 5);
        g.addEdge(4, 5, 2);
        g.addEdge(4, 6, 8);
        g.addEdge(5, 7, 4);
        g.addEdge(6, 7, 3);
        return g;
    }

    // ---------------------------------------------------------------
    // Main simulation step
    // ---------------------------------------------------------------

    public void tick(long deltaMs, boolean autoGenerate) {
        simTimeMs += deltaMs;

        if (autoGenerate && !manualControlMode) {
            double chance = emergenciesPerSecond * (deltaMs / 1000.0);
            if (rng.nextDouble() < chance) spawnRandomEmergency();
        }

        advanceAmbulances(deltaMs);
        attemptDispatches();
        advanceDoctors(deltaMs);
    }

    /**
     * Manual Control Mode: when true, NOTHING is decided automatically.
     * No background emergencies, no auto-dispatch of ambulances, no
     * auto-assignment of doctors. Every action must come from staff via
     * the Manage tab or the intake/incident dialogs. Ambulances already
     * en route still physically continue their trip either way.
     */
    public void setManualControlMode(boolean enabled) {
        this.manualControlMode = enabled;
        log(enabled ? "Manual Control Mode ENABLED: all dispatch/assignment now requires staff action."
                    : "Manual Control Mode disabled: automatic dispatch/assignment resumed.");
    }

    public boolean isManualControlMode() { return manualControlMode; }

    // ---------------------------------------------------------------
    // Patient intake: single (automatic + manual) and multi-casualty incidents
    // ---------------------------------------------------------------

    public void spawnRandomEmergency() {
        int node;
        do { node = rng.nextInt(NUM_NODES); } while (node == HOSPITAL_NODE);
        int urgency = weightedUrgency(1);
        patientCounter++;
        Patient p = new Patient("P" + patientCounter, "Patient " + patientCounter, node, urgency, simTimeMs);
        pendingCalls.enqueue(p);
        log(String.format("Emergency call: %s at node %d, urgency %s.", p.getId(), node, p.urgencyLabel()));
    }

    /** Called from the "New Patient" dialog: staff-entered name, location, and urgency. */
    public Patient addManualPatient(String name, int locationNodeId, int urgencyLevel) {
        patientCounter++;
        String id = "P" + patientCounter;
        String finalName = (name == null || name.isBlank()) ? ("Patient " + patientCounter) : name.trim();
        Patient p = new Patient(id, finalName, locationNodeId, urgencyLevel, simTimeMs);
        pendingCalls.enqueue(p);
        log(String.format("Manual intake: %s (%s) at node %d, urgency %s.", p.getId(), finalName, locationNodeId, p.urgencyLabel()));
        if (!manualControlMode) attemptDispatches(); // let it respond immediately rather than waiting for next tick
        return p;
    }

    /**
     * Reports a multi-casualty incident: creates `peopleAffected` patients
     * at one location, with urgency skewed harsher as `scale` increases.
     * Immediately attempts a priority dispatch pass so multiple idle
     * ambulances respond at once to the highest-urgency victims first
     * (rather than one call being served per tick in arrival order).
     */
    public List<Patient> createIncident(int locationNodeId, IncidentScale scale, int peopleAffected) {
        incidentCounter++;
        List<Patient> created = new ArrayList<>();
        for (int i = 0; i < peopleAffected; i++) {
            patientCounter++;
            int urgency = weightedUrgency(scale.level);
            String name = "Incident-" + incidentCounter + " Victim " + (i + 1);
            Patient p = new Patient("P" + patientCounter, name, locationNodeId, urgency, simTimeMs);
            pendingCalls.enqueue(p);
            created.add(p);
        }
        log(String.format("INCIDENT #%d reported at node %d: %s scale, %d people affected.",
                incidentCounter, locationNodeId, scale.label, peopleAffected));
        if (!manualControlMode) attemptDispatches(); // dispatch as many idle ambulances as possible right away
        return created;
    }

    /** Higher `scaleLevel` skews urgency distribution toward more critical outcomes. */
    private int weightedUrgency(int scaleLevel) {
        double r = rng.nextDouble();
        double shift = (scaleLevel - 1) * 0.15; // MASS_CASUALTY shifts distribution hardest toward critical
        if (r < 0.10 + shift) return 1;
        if (r < 0.25 + shift) return 2;
        if (r < 0.55 + shift * 0.5) return 3;
        if (r < 0.80) return 4;
        return 5;
    }

    // ---------------------------------------------------------------
    // Dispatch (automatic + manual)
    // ---------------------------------------------------------------

    /**
     * Priority dispatch pass: sorts all pending calls by urgency (most
     * critical first, same ordering the triage MinHeap uses) and greedily
     * assigns the nearest idle ambulance to each in that order. This is
     * what lets a large-scale incident grab multiple ambulances at once
     * instead of waiting one-per-tick in arrival order.
     */
    private void attemptDispatches() {
        if (manualControlMode) return;

        List<Patient> snapshot = pendingCalls.toList();
        snapshot.sort(Comparator.comparingInt(Patient::getUrgencyLevel).thenComparingLong(Patient::getCallTimestamp));

        for (Patient p : snapshot) {
            Ambulance nearest = findNearestIdleAmbulance(p.getLocationNodeId());
            if (nearest == null) continue; // no ambulance free right now; stays queued for next attempt
            pendingCalls.remove(p);
            dispatch(nearest, p);
        }
    }

    private Ambulance findNearestIdleAmbulance(int patientNode) {
        Dijkstra.Result result = Dijkstra.run(graph, patientNode);
        Ambulance best = null;
        double bestDist = Double.POSITIVE_INFINITY;
        for (Ambulance a : fleet) {
            if (a.getStatus() != Ambulance.Status.IDLE) continue;
            double d = result.distances[a.getCurrentNodeId()];
            if (d < bestDist) {
                bestDist = d;
                best = a;
            }
        }
        return best;
    }

    /** Staff manually picks which ambulance responds to which pending call (works in any mode). */
    public boolean manualDispatch(Patient p, Ambulance a) {
        if (p == null || a == null) return false;
        if (a.getStatus() != Ambulance.Status.IDLE) return false;
        if (!pendingCalls.remove(p)) return false;
        dispatch(a, p);
        log(String.format("Manual dispatch: %s assigned to %s by staff.", a.getId(), p.getId()));
        return true;
    }

    private void dispatch(Ambulance a, Patient p) {
        int idx = fleet.indexOf(a);
        Dijkstra.Result result = Dijkstra.run(graph, a.getCurrentNodeId());
        int[] path = Dijkstra.reconstructPath(result, p.getLocationNodeId());
        ambulanceRoutes.set(idx, path);
        ambulanceRouteIndex.set(idx, 0);
        ambulanceSegmentT.set(idx, 0.0);
        a.setStatus(Ambulance.Status.TO_PATIENT);
        a.setAssignedPatientId(p.getId());
        patientByAmbulance.put(a.getId(), p);
        log(String.format("%s dispatched to %s (node %d).", a.getId(), p.getId(), p.getLocationNodeId()));
    }

    private void advanceAmbulances(long deltaMs) {
        for (int i = 0; i < fleet.size(); i++) {
            Ambulance a = fleet.get(i);
            if (a.getStatus() == Ambulance.Status.IDLE) continue;

            int[] route = ambulanceRoutes.get(i);
            if (route == null || route.length < 2) continue;

            int routeIdx = ambulanceRouteIndex.get(i);
            double t = ambulanceSegmentT.get(i);
            if (routeIdx >= route.length - 1) continue;

            int fromNode = route[routeIdx];
            int toNode = route[routeIdx + 1];
            double weight = graph.getEdgeWeight(fromNode, toNode);
            if (weight <= 0) weight = 1;
            double segmentDurationMs = weight * MS_PER_WEIGHT_UNIT;

            t += deltaMs / segmentDurationMs;
            if (t >= 1.0) {
                t = 0.0;
                routeIdx++;
                a.setCurrentNodeId(toNode);
                ambulanceRouteIndex.set(i, routeIdx);
            }
            ambulanceSegmentT.set(i, t);

            if (routeIdx >= route.length - 1) {
                onAmbulanceArrived(a, i);
            }
        }
    }

    private void onAmbulanceArrived(Ambulance a, int idx) {
        Patient p = patientByAmbulance.get(a.getId());
        if (a.getStatus() == Ambulance.Status.TO_PATIENT) {
            if (p != null) p.setPickupTimestamp(simTimeMs);
            log(String.format("%s picked up %s, heading to hospital.", a.getId(), p == null ? "?" : p.getId()));
            Dijkstra.Result result = Dijkstra.run(graph, a.getCurrentNodeId());
            int[] path = Dijkstra.reconstructPath(result, HOSPITAL_NODE);
            ambulanceRoutes.set(idx, path);
            ambulanceRouteIndex.set(idx, 0);
            ambulanceSegmentT.set(idx, 0.0);
            a.setStatus(Ambulance.Status.TO_HOSPITAL);
        } else if (a.getStatus() == Ambulance.Status.TO_HOSPITAL) {
            if (p != null) {
                p.setHospitalArrivalTimestamp(simTimeMs);
                triageQueue.insert(p);
                waitingRoom.add(p);
                log(String.format("%s arrived at hospital, %s entered triage.", a.getId(), p.getId()));
            }
            a.setStatus(Ambulance.Status.IDLE);
            a.setAssignedPatientId(null);
            patientByAmbulance.remove(a.getId());
            ambulanceRoutes.set(idx, null);
        }
    }

    // ---------------------------------------------------------------
    // Doctors / triage (automatic + manual)
    // ---------------------------------------------------------------

    private void advanceDoctors(long deltaMs) {
        for (Doctor d : doctors) {
            if (d.getStatus() == Doctor.Status.BUSY) {
                long remaining = d.getBusyRemainingMs() - deltaMs;
                if (remaining <= 0) {
                    completeTreatment(d);
                } else {
                    d.setBusyRemainingMs(remaining);
                }
            }
        }
        if (manualControlMode) return;
        for (Doctor d : doctors) {
            if (d.getStatus() == Doctor.Status.AVAILABLE) {
                Patient p = pollNextTriagePatient();
                if (p != null) beginTreatment(d, p);
            }
        }
    }

    /** Extracts from the MinHeap, skipping any patient already manually pulled (lazy deletion). */
    private Patient pollNextTriagePatient() {
        while (!triageQueue.isEmpty()) {
            Patient candidate = triageQueue.extractMin();
            if (alreadyHandledPatientIds.remove(candidate.getId())) continue;
            waitingRoom.remove(candidate);
            return candidate;
        }
        return null;
    }

    private void beginTreatment(Doctor d, Patient p) {
        d.setStatus(Doctor.Status.BUSY);
        d.setCurrentPatientId(p.getId());
        long duration = (long) ((6 - p.getUrgencyLevel()) * 900 + rng.nextInt(1200));
        d.setBusyRemainingMs(duration);
        patientByDoctor.put(d.getId(), p);
        treatmentStartByDoctor.put(d.getId(), simTimeMs);
        log(String.format("%s began treating %s (%s).", d.getName(), p.getId(), p.urgencyLabel()));
    }

    /** Staff manually assigns a specific waiting patient to a specific available doctor. */
    public boolean manualAssignDoctor(Patient p, Doctor d) {
        if (p == null || d == null) return false;
        if (d.getStatus() != Doctor.Status.AVAILABLE) return false;
        if (!waitingRoom.remove(p)) return false;
        alreadyHandledPatientIds.add(p.getId()); // discard the stale heap copy when it surfaces
        beginTreatment(d, p);
        log(String.format("Manual assignment: %s -> %s by staff.", p.getId(), d.getName()));
        return true;
    }

    private void completeTreatment(Doctor d) {
        Patient p = patientByDoctor.remove(d.getId());
        Long treatmentStart = treatmentStartByDoctor.remove(d.getId());
        if (p != null) {
            p.setDischargeTimestamp(simTimeMs);
            completedResponseTimes.add(p.getResponseTime());
            treatmentHistory.add(new TreatmentRecord(p, d, treatmentStart == null ? simTimeMs : treatmentStart, simTimeMs));
            log(String.format("%s discharged %s. Response time: %.1fs.", d.getName(), p.getId(), p.getResponseTime() / 1000.0));
        }
        d.setStatus(Doctor.Status.AVAILABLE);
        d.setCurrentPatientId(null);
        d.setBusyRemainingMs(0);
    }

    // ---------------------------------------------------------------
    // Doctor roster management
    // ---------------------------------------------------------------

    public Doctor addDoctor(String name, String specialty) {
        doctorCounter++;
        Doctor d = new Doctor("D" + doctorCounter, name, specialty);
        doctors.add(d);
        log("Added " + d.getName() + " (" + d.getSpecialty() + ") to the roster.");
        return d;
    }

    /** Only allowed while the doctor is free, so nobody's active treatment is dropped. */
    public boolean removeDoctor(Doctor d) {
        if (d == null || d.getStatus() != Doctor.Status.AVAILABLE) return false;
        doctors.remove(d);
        log("Removed " + d.getName() + " from the roster.");
        return true;
    }

    private void log(String message) {
        eventLog.add(0, "[" + formatTime(simTimeMs) + "] " + message);
        while (eventLog.size() > 200) eventLog.remove(eventLog.size() - 1);
    }

    private String formatTime(long ms) {
        long totalSec = ms / 1000;
        return String.format("%02d:%02d", totalSec / 60, totalSec % 60);
    }

    // ---------------------------------------------------------------
    // Accessors used by the rendering panels
    // ---------------------------------------------------------------

    public Graph getGraph() { return graph; }
    public List<Ambulance> getFleet() { return fleet; }
    public List<Doctor> getDoctors() { return doctors; }
    public List<Patient> getWaitingRoom() { return new ArrayList<>(waitingRoom); }
    public List<Patient> getPendingCalls() {
        List<Patient> list = new ArrayList<>();
        for (Patient p : pendingCalls) list.add(p);
        return list;
    }
    public List<String> getEventLog() { return eventLog; }
    public long getSimTimeMs() { return simTimeMs; }

    public double getAverageResponseSeconds() {
        if (completedResponseTimes.isEmpty()) return 0;
        long sum = 0;
        for (long v : completedResponseTimes) sum += v;
        return (sum / (double) completedResponseTimes.size()) / 1000.0;
    }

    public int getCompletedCount() { return completedResponseTimes.size(); }

    public List<TreatmentRecord> getTreatmentReport() { return new ArrayList<>(treatmentHistory); }

    public double[] getAmbulancePosition(int idx) {
        Ambulance a = fleet.get(idx);
        int[] route = ambulanceRoutes.get(idx);
        if (route == null || route.length < 2) {
            return new double[]{NODE_X[a.getCurrentNodeId()], NODE_Y[a.getCurrentNodeId()]};
        }
        int routeIdx = Math.min(ambulanceRouteIndex.get(idx), route.length - 2);
        double t = ambulanceSegmentT.get(idx);
        int fromNode = route[routeIdx];
        int toNode = route[routeIdx + 1];
        double x = NODE_X[fromNode] + (NODE_X[toNode] - NODE_X[fromNode]) * t;
        double y = NODE_Y[fromNode] + (NODE_Y[toNode] - NODE_Y[fromNode]) * t;
        return new double[]{x, y};
    }

    public void setEmergenciesPerSecond(double v) { this.emergenciesPerSecond = v; }
    public double getEmergenciesPerSecond() { return emergenciesPerSecond; }
}
