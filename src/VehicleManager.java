import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// manager klasi gia ta oxhmata
// xorizoume ti logiki diaxeirisis apo to UI gia na einai pio organwmeno to project
public class VehicleManager {

    // lista me ola ta oxhmata pou exoume fortwsei sti mnimi
    private ArrayList<Vehicle> vehicles;

    // DataManager gia file persistence meta apo kathe allagh
    private DataManager dataManager;

    // constructor pou pairnei tin lista kai ton data manager
    public VehicleManager(ArrayList<Vehicle> vehicles, DataManager dataManager) {
        this.vehicles = vehicles;
        this.dataManager = dataManager;
    }

    // prosthiki oxhmatos me elegxo duplicate registration
    public void addVehicle(Vehicle vehicle) throws DuplicateVehicleException {
        // exception handling me custom exception gia duplicate registration
        if (findByRegistrationOrNull(vehicle.getRegistrationNumber()) != null) {
            throw new DuplicateVehicleException("Yparxei idi oxhma me ayto to registration number.");
        }

        // an den yparxei duplicate, to object mporei na mpei ston in-memory "pinaka" vehicles
        vehicles.add(vehicle);
        dataManager.saveVehicles(vehicles);
    }

    // diagrafi oxhmatos mono an den xalaei tin akeraiotita twn dedomenwn
    public void deleteVehicle(String registrationNumber, List<Rental> rentals, List<MaintenanceRecord> maintenanceRecords)
            throws VehicleNotFoundException {
        Vehicle vehicle = findByRegistration(registrationNumber);

        for (Rental rental : rentals) {
            if (rental.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
                throw new IllegalStateException(
                        "To oxhma den diagrafetai giati yparxei istoriko enoikiasewn kai theloume na to kratame.");
            }
        }

        for (MaintenanceRecord record : maintenanceRecords) {
            if (record.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
                throw new IllegalStateException(
                        "To oxhma den diagrafetai giati yparxei istoriko maintenance kai prepei na meinei syntonismeno.");
            }
        }

        vehicles.remove(vehicle);
        dataManager.saveVehicles(vehicles);
    }

    // anazitisi me registration kai custom exception
    public Vehicle findByRegistration(String registrationNumber) throws VehicleNotFoundException {
        Vehicle vehicle = findByRegistrationOrNull(registrationNumber);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Den vrethike oxhma me registration: " + registrationNumber);
        }
        return vehicle;
    }

    // helper methodos pou epistrefei null anti gia exception
    public Vehicle findByRegistrationOrNull(String registrationNumber) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getRegistrationNumber().equalsIgnoreCase(normalizeRegistration(registrationNumber))) {
                return vehicle;
            }
        }
        return null;
    }

    // lista me diathesima oxhmata se periodo
    public ArrayList<Vehicle> getAvailableVehicles(LocalDate startDate, LocalDate endDate, List<Rental> rentals,
            List<MaintenanceRecord> maintenanceRecords) {
        // o neos autos "pinakas" available einai filtrarismeno subset tou olikou vehicles list
        ArrayList<Vehicle> available = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable(startDate, endDate, rentals, maintenanceRecords)) {
                available.add(vehicle);
            }
        }

        return available;
    }

    public ArrayList<Vehicle> getVehicles() {
        // edw dinoume prosbasi ston vasiko in-memory "pinaka" oxhmatwn
        return vehicles;
    }

    // helper normalization gia consistency sto search kai duplicate check
    private String normalizeRegistration(String registrationNumber) {
        return registrationNumber == null ? "" : registrationNumber.trim().toUpperCase();
    }
}
