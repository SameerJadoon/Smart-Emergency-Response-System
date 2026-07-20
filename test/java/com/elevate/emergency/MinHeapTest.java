package com.elevate.emergency;

import com.elevate.emergency.datastructures.MinHeap;
import org.junit.jupiter.api.Test;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MinHeap: verify insert/extractMin maintain heap-order
 * property (smallest element always extracted first).
 */
public class MinHeapTest {

    @Test
    void extractsInAscendingOrder() {
        MinHeap<Integer> heap = new MinHeap<>(Comparator.naturalOrder());
        // TODO: insert several ints in random order, extractMin repeatedly,
        // assert the sequence returned is sorted ascending
    }

    @Test
    void isEmptyInitially() {
        MinHeap<Integer> heap = new MinHeap<>(Comparator.naturalOrder());
        assertTrue(heap.isEmpty());
    }
}
