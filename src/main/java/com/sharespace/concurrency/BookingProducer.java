package com.sharespace.concurrency;

/**
 * MILESTONE 4 – Producer thread.
 *
 * Simulates students submitting booking requests that enter the shared queue.
 * Demonstrates:
 *  - Producer role in Producer-Consumer pattern
 *  - Thread.sleep() to simulate request arrival rate
 *  - notifyAll() via BookingQueue.produce()
 */
public class BookingProducer implements Runnable {

    private final BookingQueue bookingQueue;
    private final int[] bookingIds;      // Booking IDs to submit
    private final long delayMs;

    public BookingProducer(BookingQueue bookingQueue, int[] bookingIds, long delayMs) {
        this.bookingQueue = bookingQueue;
        this.bookingIds   = bookingIds;
        this.delayMs      = delayMs;
    }

    @Override
    public void run() {
        System.out.println("\n[Producer] Started on thread: " + Thread.currentThread().getName());
        for (int bookingId : bookingIds) {
            try {
                Thread.sleep(delayMs);   // simulate time between student requests
                bookingQueue.produce(bookingId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Producer] Interrupted. Stopping.");
                break;
            }
        }
        bookingQueue.signalProducerDone();
        System.out.println("[Producer] Finished adding all booking requests.");
    }
}
