import java.util.*;

// Custom Exception
class InvalidCancellationException extends Exception {
    public InvalidCancellationException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// Booking Service (handles booking + cancellation)
class BookingService {

    private Map<String, Integer> inventory;
    private Map<String, Reservation> activeBookings;
    private List<Reservation> bookingHistory;

    // Stack for rollback (LIFO)
    private Stack<String> rollbackStack;

    public BookingService() {
        inventory = new HashMap<>();
        activeBookings = new HashMap<>();
        bookingHistory = new ArrayList<>();
        rollbackStack = new Stack<>();

        // Initialize inventory
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
    }

    // Create Booking
    public void createBooking(String reservationId, String guestName, String roomType) {

        if (!inventory.containsKey(roomType) || inventory.get(roomType) <= 0) {
            System.out.println("Booking failed: Room not available.");
            return;
        }

        // Allocate room (simulate room ID)
        String roomId = roomType + "-" + inventory.get(roomType);

        // Reduce inventory
        inventory.put(roomType, inventory.get(roomType) - 1);

        Reservation reservation = new Reservation(reservationId, guestName, roomType);

        activeBookings.put(reservationId, reservation);
        bookingHistory.add(reservation);

        System.out.println("Booking confirmed: " + reservation + " | Room ID: " + roomId);
    }

    // Cancel Booking (Rollback logic)
    public void cancelBooking(String reservationId) {

        try {
            // Validate existence
            if (!activeBookings.containsKey(reservationId)) {
                throw new InvalidCancellationException("Reservation not found or already cancelled.");
            }

            Reservation reservation = activeBookings.get(reservationId);

            // Step 1: Push to rollback stack
            rollbackStack.push(reservation.getReservationId());

            // Step 2: Restore inventory
            String roomType = reservation.getRoomType();
            inventory.put(roomType, inventory.get(roomType) + 1);

            // Step 3: Remove from active bookings
            activeBookings.remove(reservationId);

            System.out.println("Cancellation successful for Reservation ID: " + reservationId);

        } catch (InvalidCancellationException e) {
            System.out.println("Cancellation failed: " + e.getMessage());
        }
    }

    // Display Inventory
    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    // Display Active Bookings
    public void displayActiveBookings() {
        System.out.println("\nActive Bookings:");
        if (activeBookings.isEmpty()) {
            System.out.println("No active bookings.");
            return;
        }

        for (Reservation r : activeBookings.values()) {
            System.out.println(r);
        }
    }

    // Display Rollback Stack
    public void displayRollbackStack() {
        System.out.println("\nRollback Stack (Recent Cancellations): " + rollbackStack);
    }
}

// MAIN CLASS
public class bookmystayapp {

    public static void main(String[] args) {

        BookingService service = new BookingService();

        // Create bookings
        service.createBooking("RES101", "Rahul", "Standard");
        service.createBooking("RES102", "Anita", "Deluxe");

        // Attempt cancellation
        service.cancelBooking("RES101");

        // Invalid cancellation
        service.cancelBooking("RES999");

        // Duplicate cancellation
        service.cancelBooking("RES101");

        // Display system state
        service.displayActiveBookings();
        service.displayInventory();
        service.displayRollbackStack();
    }
}