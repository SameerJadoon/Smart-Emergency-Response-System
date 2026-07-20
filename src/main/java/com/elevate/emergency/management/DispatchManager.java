package com.elevate.emergency.management;

import com.elevate.emergency.datastructures.DoublyLinkedList;
import com.elevate.emergency.datastructures.LinkedQueue;
import com.elevate.emergency.datastructures.MinHeap;
import com.elevate.emergency.model.Ambulance;
import com.elevate.emergency.model.Patient;

/**
 * Matches incoming emergency calls to the nearest available ambulance.
 * Uses the MinHeap to rank pending calls by (urgency, proximity) and
 * a LinkedQueue to hold calls when every ambulance is currently busy.
 */
public class DispatchManager {

    private DoublyLinkedList<Ambulance> fleet;
    private MinHeap<Patient> pendingCalls;
    private LinkedQueue<Patient> waitingForAmbulance; // when fleet is fully busy
    private RoutePlanner routePlanner;

    public DispatchManager(DoublyLinkedList<Ambulance> fleet, RoutePlanner routePlanner) {
        this.fleet = fleet;
        this.routePlanner = routePlanner;
        // TODO: initialize pendingCalls (MinHeap) and waitingForAmbulance (LinkedQueue)
    }

    /** Registers a new emergency call and attempts immediate dispatch. */
    public void receiveEmergencyCall(Patient patient) {
        // TODO: insert into pendingCalls, then attempt dispatchNext()
    }

    /** Finds the nearest idle ambulance to the highest-priority pending patient and dispatches it. */
    public Ambulance dispatchNext() {
        // TODO:
        // 1. extractMin from pendingCalls
        // 2. search fleet (DoublyLinkedList) for nearest IDLE ambulance
        //    (could use BFS/Dijkstra distance from ambulance node to patient node)
        // 3. mark ambulance DISPATCHED, hand off to RoutePlanner
        // 4. if no ambulance free, push patient into waitingForAmbulance
        return null;
    }

    /** Called by CustomStack-backed undo feature to reverse the last dispatch. */
    public void undoLastDispatch() {
        // TODO: pop last dispatch action from a CustomStack<DispatchRecord>
    }
}
