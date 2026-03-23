import java.util.*;

// Represents an optional service
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Manages add-on services for reservations
class AddOnServiceManager {

    private Map<String, List<AddOnService>> reservationServicesMap;

    public AddOnServiceManager() {
        reservationServicesMap = new HashMap<>();
    }

    public void addServices(String reservationId, List<AddOnService> services) {
        reservationServicesMap.putIfAbsent(reservationId, new ArrayList<>());
        reservationServicesMap.get(reservationId).addAll(services);
    }

    public List<AddOnService> getServices(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
    }

    public double calculateTotalCost(String reservationId) {
        List<AddOnService> services = reservationServicesMap.get(reservationId);
        if (services == null) return 0.0;

        double total = 0;
        for (AddOnService service : services) {
            total += service.getCost();
        }
        return total;
    }

    public void displayServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Selected Add-On Services:");
        for (AddOnService service : services) {
            System.out.println("- " + service);
        }
    }
}

// Reservation class (core booking remains unchanged)
class Reservation {
    private String reservationId;
    private String guestName;

    public Reservation(String reservationId, String guestName) {
        this.reservationId = reservationId;
        this.guestName = guestName;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }
}

// MAIN CLASS (Renamed)
public class bookmystayapp {

    public static void main(String[] args) {

        // Simulated existing reservation
        Reservation reservation = new Reservation("RES101", "Rahul");

        // Create Add-On Services
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 1200);
        AddOnService spa = new AddOnService("Spa Access", 2000);

        // Guest selects services
        List<AddOnService> selectedServices = new ArrayList<>();
        selectedServices.add(breakfast);
        selectedServices.add(airportPickup);
        selectedServices.add(spa);

        // Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Attach services
        manager.addServices(reservation.getReservationId(), selectedServices);

        // Output
        System.out.println("Reservation ID: " + reservation.getReservationId());
        System.out.println("Guest Name: " + reservation.getGuestName());

        manager.displayServices(reservation.getReservationId());

        double totalCost = manager.calculateTotalCost(reservation.getReservationId());
        System.out.println("Total Add-On Cost: ₹" + totalCost);

        System.out.println("\nCore booking and inventory remain unaffected.");
    }
}