package com.elevate.emergency.model;

/**
 * Represents an intersection/vertex on the city road Graph.
 */
public class RoadNode {

    private final int id;
    private String label; // e.g. "Main Street & 5th Ave"

    public RoadNode(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() { return id; }
    public String getLabel() { return label; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoadNode)) return false;
        return this.id == ((RoadNode) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
