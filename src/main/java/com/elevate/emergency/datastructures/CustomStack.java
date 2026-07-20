package com.elevate.emergency.datastructures;

import java.util.Arrays;

/**
 * Custom-built stack (LIFO) backed by a dynamic array.
 * Used for: undo-last-dispatch feature, and route backtracking.
 *
 *  - push(T item) -> O(1) amortized
 *  - pop()         -> O(1)
 *  - peek()         -> O(1)
 */
public class CustomStack<T> {

    private Object[] elements;
    private int size;

    public CustomStack() {
        elements = new Object[16];
        size = 0;
    }

    public void push(T item) {
        if (size == elements.length) elements = Arrays.copyOf(elements, elements.length * 2);
        elements[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (size == 0) return null;
        T item = (T) elements[--size];
        elements[size] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return size == 0 ? null : (T) elements[size - 1];
    }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }
}
