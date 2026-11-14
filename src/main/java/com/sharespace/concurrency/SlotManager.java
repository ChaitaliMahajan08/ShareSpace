package com.sharespace.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * MILESTONE 3 – Shared resource slot manager.
 *
 * Maintains the available unit count for a single resource.
 * All check-and-reserve operations are synchronized to prevent race conditions.
 * Demonstrates:
 *  - synchronized methods
 *  - critical section protection
 *  - Thread.sleep() for simulated processing delay
 *  - Thread names printed per operation
 */
public class SlotManager {

    private final String resourceName;
    private int availableSlots;
    private final int totalSlots;

    // Atomic counter tracks how many reservations succeeded (thread-safe read)
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount    = new AtomicInteger(0);

    public SlotManager(String resourceName, int totalSlots) {
        this.resourceName  = resourceName;
        this.totalSlots    = totalSlots;
        this.availableSlots = totalSlots;
    }

    /**
     * Attempts to reserve one slot for a student.
     * This method is SYNCHRONIZED — only one thread executes this critical
     * section at a time, preventing overbooking.
     *
     * IMPORTANT: The check-and-decrement is a single atomic operation here.
     * If we did this without synchronization, two threads could both read
     * availableSlots > 0 and both decrement, causing negative availability.
     *
     * @param studentName Name of the student attempting to reserve.
     * @return true if reservation was successful, false otherwise.
     */
    public synchronized boolean reserve(String studentName) {
        String thread = Thread.currentThread().getName();
        System.out.println("[Thread: " + thread + "] " + studentName
                + " is attempting to reserve '" + resourceName + "' ...");

        // Simulate processing delay inside synchronized block
        try { Thread.sleep(100); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Thread: " + thread + "] Interrupted while processing for " + studentName);
            return false;
        }

        if (availableSlots > 0) {
            availableSlots--;
            successCount.incrementAndGet();
            System.out.println("[Thread: " + thread + "] ✔ SUCCESS  — " + studentName
                    + " reserved a slot. Remaining: " + availableSlots + "/" + totalSlots);
            return true;
        } else {
            failCount.incrementAndGet();
            System.out.println("[Thread: " + thread + "] ✘ FAILED   — " + studentName
                    + " could not reserve. No slots available.");
            return false;
        }
    }

    /**
     * Releases a slot (simulates a cancellation or return).
     */
    public synchronized void release(String studentName) {
        if (availableSlots < totalSlots) {
            availableSlots++;
            System.out.println("[SlotManager] Slot released by " + studentName
                    + ". Available: " + availableSlots + "/" + totalSlots);
        }
    }

    public synchronized int getAvailableSlots() { return availableSlots; }
    public int getTotalSlots()      { return totalSlots; }
    public int getSuccessCount()    { return successCount.get(); }
    public int getFailCount()       { return failCount.get(); }
    public String getResourceName() { return resourceName; }

    public synchronized void reset() {
        availableSlots = totalSlots;
        successCount.set(0);
        failCount.set(0);
    }
}
