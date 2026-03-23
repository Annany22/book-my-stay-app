import java.util.*;

// Reservation class (core booking entity)
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
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType;
    }
}

// Booking History (stores confirmed reservations)
class BookingHistory {
    private List<Reservation> confirmedBookings;

    public BookingHistory() {
        confirmedBookings = new ArrayList<>();
    }

    // Add confirmed booking
    public void addBooking(Reservation reservation) {
        confirmedBookings.add(reservation);
    }

    // Retrieve all bookings
    public List<Reservation> getAllBookings() {
        return confirmedBookings;
    }
}

// Reporting Service (separate from storage)
class BookingReportService {

    // Display all bookings
    public void displayAllBookings(List<Reservation> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("No booking history available.");
            return;
        }

        System.out.println("\n=== Booking History ===");
        for (Reservation r : bookings) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummaryReport(List<Reservation> bookings) {
        System.out.println("\n=== Booking Summary Report ===");

        System.out.println("Total Bookings: " + bookings.size());

        // Count bookings by room type
        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : bookings) {
            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("Bookings by Room Type:");
        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue());
        }
    }
}

// MAIN CLASS
public class bookmystayapp {

    public static void main(String[] args) {

        // Booking history storage
        BookingHistory history = new BookingHistory();

        // Simulate confirmed bookings
        Reservation r1 = new Reservation("RES101", "Rahul", "Deluxe");
        Reservation r2 = new Reservation("RES102", "Anita", "Standard");
        Reservation r3 = new Reservation("RES103", "Vikram", "Deluxe");

        // Add to history (in order)
        history.addBooking(r1);
        history.addBooking(r2);
        history.addBooking(r3);

        // Reporting
        BookingReportService reportService = new BookingReportService();

        // Admin views all bookings
        reportService.displayAllBookings(history.getAllBookings());

        // Admin generates summary report
        reportService.generateSummaryReport(history.getAllBookings());
    }
}