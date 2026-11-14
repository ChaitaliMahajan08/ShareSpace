package com.sharespace.concurrency;

import java.util.LinkedList;
import java.util.Queue;

/**
 * MILESTONE 4 – Shared booking request queue.
 *
 * Used by the Producer-Consumer pattern.
 * Access is synchronized using wait()/notify().
 *
 * Demonstrates:
 *  - Synchronized shared state
 *  - wait() when queue is empty (Consumer waits)
 *  - notify()/notifyAll() when new item is added (Producer wakes Consumer)
 *  - while() loop around wait() (guards against spurious wakeups)
 *  - Correct monitor ownership (all wait/notify inside synchronized blocks)
 */
public class BookingQueue {

    private final Queue<Integer> queue;   // Queue of booking IDs pending processing
    private final int maxCapacity;
    private volatile boolean producerDone = false;

    public BookingQueue(int maxCapacity) {
        this.queue       = new LinkedList<>();
        this.maxCapacity = maxCapacity;
    }

    /**
     * Producer calls this to add a booking ID.
     * Uses notifyAll() to wake waiting consumer threads.
     */
    public synchronized void produce(int bookingId) throws InterruptedException {
        // Wait if queue is full (bounded queue)
        while (queue.size() >= maxCapacity) {
            System.out.println("[Producer | " + Thread.currentThread().getName()
                    + "] Queue full — waiting...");
            wait();   // releases monitor, waits for consumer to notify
        }
        queue.add(bookingId);
        System.out.println("[Producer | " + Thread.currentThread().getName()
                + "] Added booking #" + bookingId + " to queue. Queue size: " + queue.size());
        notifyAll();  // wake up any waiting consumers
    }

    /**
     * Consumer calls this to retrieve and remove the next booking ID.
     * Uses wait() when queue is empty. The while() loop re-checks
     * the condition after wakeup to handle spurious wakeups.
     */
    public synchronized int consume() throws InterruptedException {
        // while() — not if() — to guard against spurious wakeups (Milestone 4 requirement)
        while (queue.isEmpty() && !producerDone) {
            System.out.println("[Consumer | " + Thread.currentThread().getName()
                    + "] Queue empty — waiting for new bookings...");
            wait();   // releases monitor
        }

        if (queue.isEmpty()) {
            return -1;  // Signal: no more items and producer is done
        }

        int bookingId = queue.poll();
        System.out.println("[Consumer | " + Thread.currentThread().getName()
                + "] Retrieved booking #" + bookingId + " from queue. Queue size: " + queue.size());
        notifyAll();  // wake any waiting producers
        return bookingId;
    }

    /**
     * Producer signals it has finished adding items.
     */
    public synchronized void signalProducerDone() {
        this.producerDone = true;
        notifyAll();  // wake all consumers so they can exit cleanly
        System.out.println("[Queue] Producer signaled done. No more bookings will be added.");
    }

    public synchronized boolean isEmpty()         { return queue.isEmpty(); }
    public synchronized int size()                { return queue.size(); }
    public boolean isProducerDone()               { return producerDone; }
}
