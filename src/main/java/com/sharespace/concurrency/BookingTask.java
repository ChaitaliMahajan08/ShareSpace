package com.sharespace.concurrency;

/**
 * MILESTONE 3 – Booking task (Runnable).
 *
 * Each BookingTask represents one student thread trying to reserve
 * a slot from the shared SlotManager.
 *
 * Demonstrates:
 *  - Implementing Runnable
 *  - Thread.start() vs run() (see bottom of class for explanation)
 *  - Thread.sleep() for simulated delay between attempts
 *  - Thread.currentThread().getName()
 *  - Thread.interrupt() handling
 */
public class BookingTask implements Runnable {

    private final String studentName;
    private final SlotManager slotManager;
    private boolean reservationResult = false;

    public BookingTask(String studentName, SlotManager slotManager) {
        this.studentName  = studentName;
        this.slotManager  = slotManager;
    }

    /**
     * When this Runnable is started via Thread.start(), the JVM creates a new
     * OS-level thread and calls run() on it concurrently.
     *
     * If you call run() directly (without Thread.start()), it executes
     * synchronously on the CALLING thread — no new thread is created.
     * That is the key difference between start() and run().
     */
    @Override
    public void run() {
        // Small random delay so threads don't all start at the exact same nanosecond
        try {
            Thread.sleep((long)(Math.random() * 50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[" + Thread.currentThread().getName() + "] "
                    + studentName + " was interrupted before attempting booking.");
            return;
        }

        reservationResult = slotManager.reserve(studentName);
    }

    public String getStudentName()     { return studentName; }
    public boolean isReservationSuccessful() { return reservationResult; }

    /*
     * ============================================================
     * CONCEPT NOTE: Thread.start() vs Thread.run()
     * ============================================================
     * Thread.start():
     *   - Creates a NEW thread of execution.
     *   - The JVM/OS schedules the new thread.
     *   - run() is called concurrently with the calling thread.
     *   - Required for true parallelism/concurrency.
     *
     * Thread.run() (direct call):
     *   - Does NOT create a new thread.
     *   - run() is called like any other method on the CURRENT thread.
     *   - Sequential execution — defeats the purpose of Runnable.
     *
     * Summary: Always use start() for concurrent execution.
     * ============================================================
     */
}
