import java.util.*;

// Booking Request (Represents input from guest)
class BookingRequest {
    String reservationId;
    String guestName;
    String roomType;

    public BookingRequest(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// Thread-safe Booking System
class ConcurrentBookingProcessor {

    private Map<String, Integer> inventory = new HashMap<>();
    private Queue<BookingRequest> bookingQueue = new LinkedList<>();

    public ConcurrentBookingProcessor() {
        // Initialize inventory
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
    }

    // Add booking request (Producer)
    public synchronized void addRequest(BookingRequest request) {
        bookingQueue.add(request);
        System.out.println("Request added: " + request.reservationId + " (" + request.roomType + ")");
    }

    // Process booking request (Consumer)
    public void processRequest() {
        while (true) {
            BookingRequest request;

            // Critical section: safely fetch request
            synchronized (this) {
                if (bookingQueue.isEmpty()) {
                    return;
                }
                request = bookingQueue.poll();
            }

            // Critical section: inventory update
            synchronized (this) {
                if (inventory.containsKey(request.roomType) &&
                        inventory.get(request.roomType) > 0) {

                    // Allocate room
                    inventory.put(request.roomType,
                            inventory.get(request.roomType) - 1);

                    System.out.println(Thread.currentThread().getName() +
                            " processed booking: " + request.reservationId +
                            " | " + request.roomType);

                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " failed booking: " + request.reservationId +
                            " | No availability");
                }
            }
        }
    }

    public void displayInventory() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}

// Worker Thread
class BookingWorker extends Thread {

    private ConcurrentBookingProcessor processor;

    public BookingWorker(ConcurrentBookingProcessor processor, String name) {
        super(name);
        this.processor = processor;
    }

    @Override
    public void run() {
        processor.processRequest();
    }
}

// MAIN CLASS
public class bookmystayapp {

    public static void main(String[] args) {

        ConcurrentBookingProcessor processor = new ConcurrentBookingProcessor();

        // Simulate multiple guest requests
        processor.addRequest(new BookingRequest("RES101", "Rahul", "Standard"));
        processor.addRequest(new BookingRequest("RES102", "Anita", "Deluxe"));
        processor.addRequest(new BookingRequest("RES103", "Vikram", "Standard"));
        processor.addRequest(new BookingRequest("RES104", "Neha", "Standard")); // extra request

        // Create multiple threads (simulating concurrent users)
        Thread t1 = new BookingWorker(processor, "Thread-1");
        Thread t2 = new BookingWorker(processor, "Thread-2");
        Thread t3 = new BookingWorker(processor, "Thread-3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for threads to finish
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final system state
        processor.displayInventory();
    }
}