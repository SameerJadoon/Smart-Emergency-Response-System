package com.elevate.emergency.datastructures;

import java.util.Iterator;

/**
 * Custom-built doubly linked list.
 * Used for: ambulance fleet management, doctor availability list,
 * and the event log (traversable forward/backward).
 *
 *  - addFirst(T item) / addLast(T item) -> O(1)
 *  - remove(T item)                       -> O(n)
 */
public class DoublyLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T item) {
        Node<T> node = new Node<>(item);
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    public void addLast(T item) {
        Node<T> node = new Node<>(item);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
    }

    public boolean remove(T item) {
        Node<T> cur = head;
        while (cur != null) {
            if (cur.data.equals(item)) {
                if (cur.prev != null) cur.prev.next = cur.next; else head = cur.next;
                if (cur.next != null) cur.next.prev = cur.prev; else tail = cur.prev;
                size--;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

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
}
