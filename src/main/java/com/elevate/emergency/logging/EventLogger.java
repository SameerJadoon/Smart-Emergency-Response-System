package com.elevate.emergency.logging;

import com.elevate.emergency.datastructures.DoublyLinkedList;

/**
 * Records a full audit trail of everything that happens in the simulation
 * (calls received, dispatches, arrivals, treatments, discharges) so it can
 * be traversed forward or backward for the final report.
 */
public class EventLogger {

    public static class LogEntry {
        long timestamp;
        String description;
        LogEntry(long timestamp, String description) {
            this.timestamp = timestamp;
            this.description = description;
        }
    }

    private DoublyLinkedList<LogEntry> events;

    public EventLogger() {
        this.events = new DoublyLinkedList<>();
    }

    public void log(long timestamp, String description) {
        events.addLast(new LogEntry(timestamp, description));
    }

    // TODO: printAllEvents(), exportToFile(String path)
}
