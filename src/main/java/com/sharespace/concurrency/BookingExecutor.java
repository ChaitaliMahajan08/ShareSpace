package com.sharespace.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MILESTONE 3 + MILESTONE 4 – Concurrency demonstrations.
 *
 * This class contains two independent demonstrations:
 *
 * 1. runConcurrentBookingSimulation():
 *    - 5 student threads compete for 2 slots (Milestone 3)
 *    - Uses Thread, Runnable, start(), join(), sleep(), interrupt()
 *    - Uses synchronized methods in SlotManager
 *
 * 2. runProducerConsumerDemo():
 *    - Producer adds booking IDs to shared queue (Milestone 4)
 *    - Consumer processes them using wait()/notifyAll()
 *    - Uses ExecutorService with fixed thread pool
 *    - Uses Callable + Future to retrieve pending booking count
 *    - Uses shutdown() and awaitTermination()
 */
public class BookingExecutor {

    // ============================================================
    // MILESTONE 3: Concurrent Booking Simulation
    // ============================================================

    /**
     * Simulates 5 students competing for 2 available slots.
     * Demonstrates synchronized critical section, start(), join(), sleep(), interrupt().
     */
    public static void runConcurrentBookingSimulation() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  MILESTONE 3: CONCURRENT BOOKING SIMULATION");
        System.out.println("=".repeat(60));
        System.out.println("  Resource: 'Canon DSLR Camera'");
        System.out.println("  Available Slots: 2");
        System.out.println("  Students Attempting: 5");
        System.out.println("  Expected: 2 succeed, 3 fail (no overbooking)");
        System.out.println("=".repeat(60) + "\n");

        SlotManager slotManager = new SlotManager("Canon DSLR Camera", 2);

        String[] students = {
            "Alice Johnson", "Bob Smith", "Carol Davis",
            "David Wilson", "Eva Martinez"
        };

        List<Thread> threads = new ArrayList<>();
        List<BookingTask> tasks = new ArrayList<>();

        // Create and name each thread (Milestone 3: Thread.start())
        for (int i = 0; i < students.length; i++) {
            BookingTask task = new BookingTask(students[i], slotManager);
            tasks.add(task);
            Thread thread = new Thread(task, "StudentThread-" + (i + 1));
            threads.add(thread);
        }

        // Demonstrate interrupt() — interrupt thread 5 before starting (Milestone 3)
        Thread interruptTarget = threads.get(4);
        interruptTarget.interrupt();  // Eva's thread will be interrupted

        // Start all threads using start() — creates new OS threads (Milestone 3)
        // KEY: start() creates a new thread; calling run() directly does NOT.
        System.out.println("--- Starting all student threads using Thread.start() ---\n");
        for (Thread t : threads) {
            t.start();  // Milestone 3: start()
        }

        // Wait for all threads to finish using join() (Milestone 3)
        System.out.println("\n--- Main thread waiting for all booking threads (join()) ---");
        for (Thread t : threads) {
            try {
                t.join();  // Milestone 3: join()
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Print results
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  SIMULATION RESULTS");
        System.out.println("=".repeat(60));
        System.out.println("  Successful Reservations : " + slotManager.getSuccessCount());
        System.out.println("  Failed Reservations     : " + slotManager.getFailCount());
        System.out.println("  Remaining Slots         : " + slotManager.getAvailableSlots()
                + "/" + slotManager.getTotalSlots());
        System.out.println("  Overbooking occurred    : "
                + (slotManager.getAvailableSlots() < 0 ? "YES (BUG!)" : "NO (Correct)"));
        System.out.println("=".repeat(60));

        for (BookingTask task : tasks) {
            System.out.printf("  %-20s -> %s%n",
                    task.getStudentName(),
                    task.isReservationSuccessful() ? "RESERVED" : "FAILED / INTERRUPTED");
        }
        System.out.println("=".repeat(60) + "\n");
    }

    // ============================================================
    // MILESTONE 4: Producer-Consumer + ExecutorService
    // ============================================================

    /**
     * Demonstrates Producer-Consumer pattern with shared BookingQueue,
     * ExecutorService, Callable, and Future.
     */
    public static void runProducerConsumerDemo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  MILESTONE 4: PRODUCER-CONSUMER BOOKING QUEUE");
        System.out.println("=".repeat(60));

        BookingQueue queue = new BookingQueue(10);
        AtomicInteger processedCount = new AtomicInteger(0);

        // Booking IDs to be processed (simulating real pending booking IDs)
        int[] pendingBookingIds = {101, 102, 103, 104, 105, 106};

        // --- Producer-Consumer via plain Threads ---
        Thread producerThread = new Thread(
                new BookingProducer(queue, pendingBookingIds, 200),
                "ProducerThread"
        );
        Thread consumerThread = new Thread(
                new BookingConsumer(queue, processedCount, 350),
                "ConsumerThread"
        );

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n[Queue Demo] Producer-Consumer finished.");
        System.out.println("[Queue Demo] Total processed: " + processedCount.get());

        // --- ExecutorService with Fixed Thread Pool (Milestone 4) ---
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  MILESTONE 4: EXECUTORSERVICE DEMO");
        System.out.println("=".repeat(60));

        ExecutorService executor = Executors.newFixedThreadPool(3);  // Milestone 4

        // Submit Runnable tasks
        for (int i = 1; i <= 3; i++) {
            final int taskNum = i;
            executor.submit(() -> {   // Milestone 4: Runnable via executor
                System.out.println("[ExecutorService | " + Thread.currentThread().getName()
                        + "] Processing notification for booking #" + taskNum);
                try { Thread.sleep(100); } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Submit a Callable task that returns pending booking count (Milestone 4)
        Callable<Integer> pendingCountTask = () -> {
            System.out.println("[Callable | " + Thread.currentThread().getName()
                    + "] Counting processed bookings...");
            Thread.sleep(150);
            return processedCount.get();   // Returns meaningful result
        };

        Future<Integer> future = executor.submit(pendingCountTask);  // Milestone 4: Future

        try {
            Integer result = future.get();  // Milestone 4: Future.get()
            System.out.println("[Future Result] Total bookings processed by consumer: " + result);
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("[Future] Error retrieving result: " + e.getMessage());
        }

        // Shutdown executor cleanly (Milestone 4)
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {  // Milestone 4
                System.out.println("[Executor] Force shutting down...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("[Executor] ExecutorService shut down cleanly.");
        System.out.println("=".repeat(60) + "\n");
    }
}
