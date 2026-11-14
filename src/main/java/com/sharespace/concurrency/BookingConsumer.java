package com.sharespace.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * MILESTONE 4 – Consumer thread.
 *
 * Processes booking requests from the shared queue.
 * Demonstrates:
 *  - Consumer role in Producer-Consumer pattern
 *  - wait() via BookingQueue.consume() when queue is empty
 *  - Thread.sleep() simulating processing time
 *  - AtomicInteger for thread-safe processed count
 */
public class BookingConsumer implements Runnable {

    private final BookingQueue bookingQueue;
    private final AtomicInteger processedCount;
    private final long processingDelayMs;

    public BookingConsumer(BookingQueue bookingQueue,
                           AtomicInteger processedCount,
                           long processingDelayMs) {
        this.bookingQueue       = bookingQueue;
        this.processedCount     = processedCount;
        this.processingDelayMs  = processingDelayMs;
    }

    @Override
    public void run() {
        System.out.println("[Consumer] Started on thread: " + Thread.currentThread().getName());
        while (true) {
            try {
                int bookingId = bookingQueue.consume();
                if (bookingId == -1) {
                    // Queue is empty and producer is done
                    System.out.println("[Consumer | " + Thread.currentThread().getName()
                            + "] No more bookings. Exiting.");
                    break;
                }

                // Simulate processing time
                Thread.sleep(processingDelayMs);

                processedCount.incrementAndGet();
                System.out.println("[Consumer | " + Thread.currentThread().getName()
                        + "] Processed booking #" + bookingId
                        + ". Total processed: " + processedCount.get());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Consumer | " + Thread.currentThread().getName()
                        + "] Interrupted. Stopping.");
                break;
            }
        }
    }
}
