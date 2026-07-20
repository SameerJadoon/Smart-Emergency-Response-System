package com.elevate.emergency.model;

/**
 * Represents a weighted, directed or undirected road connecting two nodes.
 * Weight can represent distance or travel time and may be updated
 * dynamically by TrafficUpdater to simulate congestion.
 */
public class RoadEdge {

    private final int fromNodeId;
    private final int toNodeId;
    private double weight; // distance or time cost; mutable for dynamic traffic

    public RoadEdge(int fromNodeId, int toNodeId, double weight) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.weight = weight;
    }

    public int getFromNodeId() { return fromNodeId; }
    public int getToNodeId() { return toNodeId; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
