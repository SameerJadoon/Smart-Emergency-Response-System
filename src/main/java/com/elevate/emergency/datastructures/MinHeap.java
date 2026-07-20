package com.elevate.emergency.datastructures;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Custom-built binary Min-Heap used as a priority queue.
 * Used for: hospital triage queue (lower urgency number = higher priority)
 * and Dijkstra's algorithm's internal frontier.
 *
 * Backed by a dynamic array (no java.util.PriorityQueue used).
 *
 *  - insert(T item)      -> O(log n)
 *  - extractMin()        -> O(log n)
 *  - peek()               -> O(1)
 *  - isEmpty()             -> O(1)
 */
public class MinHeap<T> {

    private Object[] elements;
    private int size;
    private final Comparator<T> comparator;

    public MinHeap(Comparator<T> comparator) {
        this.elements = new Object[16];
        this.size = 0;
        this.comparator = comparator;
    }

    public void insert(T item) {
        if (size == elements.length) resize();
        elements[size] = item;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) throw new NoSuchElementException("Heap is empty");
        T min = (T) elements[0];
        size--;
        elements[0] = elements[size];
        elements[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return size == 0 ? null : (T) elements[0];
    }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (comparator.compare((T) elements[index], (T) elements[parent]) < 0) {
                swap(index, parent);
                index = parent;
            } else break;
        }
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int smallest = index;
            if (left < size && comparator.compare((T) elements[left], (T) elements[smallest]) < 0) smallest = left;
            if (right < size && comparator.compare((T) elements[right], (T) elements[smallest]) < 0) smallest = right;
            if (smallest == index) break;
            swap(index, smallest);
            index = smallest;
        }
    }

    private void swap(int i, int j) {
        Object tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
    }

    private void resize() {
        elements = Arrays.copyOf(elements, elements.length * 2);
    }

    /**
     * Removes and returns the first element matching the given predicate,
     * re-inserting all other elements afterward. A binary heap has no
     * native support for arbitrary-key deletion (only the root is cheap
     * to remove); this trades O(log n) for O(n log n) to support manual
     * overrides (e.g. staff manually assigning a specific patient to a
     * specific doctor out of triage order) without maintaining extra
     * index bookkeeping.
     */
    public T removeMatching(java.util.function.Predicate<T> predicate) {
        java.util.List<T> setAside = new java.util.ArrayList<>();
        T found = null;
        while (!isEmpty()) {
            T item = extractMin();
            if (found == null && predicate.test(item)) {
                found = item;
            } else {
                setAside.add(item);
            }
        }
        for (T item : setAside) insert(item);
        return found;
    }

    /** Returns a snapshot list of all elements in heap order (not fully sorted). */
    public java.util.List<T> toList() {
        java.util.List<T> list = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T item = (T) elements[i];
            list.add(item);
        }
        return list;
    }
}
