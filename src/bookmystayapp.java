import java.io.*;
import java.util.*;

// Reservation class (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

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

// Wrapper class to persist full system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    Map<String, Integer> inventory;
    List<Reservation> bookingHistory;

    public SystemState(Map<String, Integer> inventory, List<Reservation> bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // Save state to file
    public void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("System state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state from file
    public SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No previous state found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state. Starting with safe defaults.");
        }

        return null;
    }
}

// Booking Service
class BookingService {

    private Map<String, Integer> inventory;
    private List<Reservation> bookingHistory;

    public BookingService() {
        inventory = new HashMap<>();
        bookingHistory = new ArrayList<>();

        // Default inventory
        inventory.put("Standard", 2);
        inventory.put("Deluxe", 1);
    }

    // Restore state
    public void restore(SystemState state) {
        if (state != null) {
            this.inventory = state.inventory;
            this.bookingHistory = state.bookingHistory;
        }
    }

    // Create booking
    public void createBooking(String id, String name, String type) {
        if (!inventory.containsKey(type) || inventory.get(type) <= 0) {
            System.out.println("Booking failed: No availability.");
            return;
        }

        inventory.put(type, inventory.get(type) - 1);

        Reservation r = new Reservation(id, name, type);
        bookingHistory.add(r);

        System.out.println("Booking confirmed: " + r);
    }

    public SystemState getState() {
        return new SystemState(inventory, bookingHistory);
    }

    public void displayState() {
        System.out.println("\nInventory:");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println("\nBooking History:");
        if (bookingHistory.isEmpty()) {
            System.out.println("No bookings.");
        } else {
            for (Reservation r : bookingHistory) {
                System.out.println(r);
            }
        }
    }
}

// MAIN CLASS
public class bookmystayapp {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();
        BookingService service = new BookingService();

        // Step 1: Load previous state
        SystemState loadedState = persistence.load();
        service.restore(loadedState);

        // Step 2: Perform operations
        service.createBooking("RES201", "Rahul", "Standard");
        service.createBooking("RES202", "Anita", "Deluxe");

        // Step 3: Display current state
        service.displayState();

        // Step 4: Save state before shutdown
        persistence.save(service.getState());
    }
}