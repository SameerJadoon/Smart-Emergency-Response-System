package com.elevate.emergency.datastructures;

import java.util.Iterator;

/**
 * Custom-built FIFO queue backed by a singly linked list.
 * Used for: waiting room (same-urgency patients), pending emergency calls
 * when all ambulances are busy, and as BFS's frontier.
 *
 * Also supports iteration (for displaying pending calls in the GUI) and
 * removal by reference (for manual dispatch overrides).
 *
 *  - enqueue(T item) -> O(1)
 *  - dequeue()        -> O(1)
 *  - remove(T item)    -> O(n)
 *  - peek()            -> O(1)
 *  - isEmpty()          -> O(1)
 */
public class LinkedQueue<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void enqueue(T item) {
        Node<T> node = new Node<>(item);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public T dequeue() {
        if (head == null) return null;
        T data = head.data;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return data;
    }

    /** Removes a specific item from anywhere in the queue (used for manual dispatch overrides). */
    public boolean remove(T item) {
        Node<T> prev = null, cur = head;
        while (cur != null) {
            if (cur.data.equals(item)) {
                if (prev == null) head = cur.next; else prev.next = cur.next;
                if (cur == tail) tail = prev;
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    public T peek() {
        return head == null ? null : head.data;
    }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> current = head;
            public boolean hasNext() { return current != null; }
            public T next() {
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    /** Returns a snapshot list of all elements in FIFO order, without removing them. */
    public java.util.List<T> toList() {
        java.util.List<T> list = new java.util.ArrayList<>();
        Node<T> cur = head;
        while (cur != null) {
            list.add(cur.data);
            cur = cur.next;
        }
        return list;
    }
}
