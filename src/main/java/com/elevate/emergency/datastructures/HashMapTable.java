package com.elevate.emergency.datastructures;

/**
 * Custom-built hash map (separate chaining) used for O(1) average lookups:
 * Patient ID -> Patient, Ambulance ID -> Ambulance, Doctor ID -> Doctor.
 *
 * Note: you can use java.util.HashMap directly in management/ classes for
 * simplicity, and keep this custom version specifically to demonstrate
 * hashing (hash function, collision handling via chaining) in your report.
 *
 * Core operations:
 *  - put(K key, V value) -> O(1) average
 *  - get(K key)           -> O(1) average
 *  - remove(K key)         -> O(1) average
 */
public class HashMapTable<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next; // for chaining on collision
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    private Entry<?, ?>[] buckets;
    private int capacity;
    private int size;

    public HashMapTable() {
        this.capacity = 16;
        this.buckets = new Entry<?, ?>[capacity];
    }

    private int hash(K key) {
        // TODO: return Math.abs(key.hashCode()) % capacity;
        return 0;
    }

    public void put(K key, V value) {
        // TODO: compute bucket index, walk chain, update or append
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        // TODO: compute bucket index, walk chain, return match
        return null;
    }

    public boolean remove(K key) {
        // TODO
        return false;
    }
}
