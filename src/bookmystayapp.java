import java.util.*;

// Custom Exception for Invalid Booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
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

// Validator Class (Fail-Fast Design)
class InvalidBookingValidator {

    private static final Set<String> VALID_ROOM_TYPES =
            new HashSet<>(Arrays.asList("Standard", "Deluxe", "Suite"));

    public static void validate(String roomType, Map<String, Integer> inventory)
            throws InvalidBookingException {

        // Validate room type
        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        // Validate availability
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Room type not found in inventory.");
        }

        if (inventory.get(roomType) <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }
    }
}

// Booking Service (Safe state handling)
class BookingService {

    private Map<String, Integer> inventory;
    private List<Reservation> bookingHistory;

    public BookingService() {
        inventory = new HashMap<>();
        bookingHistory = new ArrayList<>();

        // Initialize inventory
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
        inventory.put("Suite", 0); // intentionally zero to test validation
    }

    public void createBooking(String reservationId, String guestName, String roomType) {
        try {
            // Step 1: Validate input (Fail Fast)
            InvalidBookingValidator.validate(roomType, inventory);

            // Step 2: Safe inventory update
            inventory.put(roomType, inventory.get(roomType) - 1);

            // Step 3: Create reservation
            Reservation reservation = new Reservation(reservationId, guestName, roomType);

            // Step 4: Store in history
            bookingHistory.add(reservation);

            System.out.println("Booking successful: " + reservation);

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public void displayBookingHistory() {
        System.out.println("\nBooking History:");
        if (bookingHistory.isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }

        for (Reservation r : bookingHistory) {
            System.out.println(r);
        }
    }
}

// MAIN CLASS
public class bookmystayapp {

    public static void main(String[] args) {

        BookingService service = new BookingService();

        // Valid booking
        service.createBooking("RES101", "Rahul", "Standard");

        // Invalid room type
        service.createBooking("RES102", "Anita", "Premium");

        // No availability case
        service.createBooking("RES103", "Vikram", "Suite");

        // Valid booking
        service.createBooking("RES104", "Neha", "Deluxe");

        // Inventory exhausted case
        service.createBooking("RES105", "Amit", "Deluxe");

        // Display system state
        service.displayInventory();
        service.displayBookingHistory();
    }
}