import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

// manager klasi gia ola ta business rules twn enoikiasewn
// edw mazeyontai availability, cost logic, loyalty logic kai rental completion
public class RentalManager {

    // lista me oles tis enoikiaseis
    private ArrayList<Rental> rentals;

    // kratame references sta ypoloipa domain data gia na kanoume reconnect kai save
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<MaintenanceRecord> maintenanceRecords;

    // DataManager gia file persistence
    private DataManager dataManager;

    // constructor
    public RentalManager(ArrayList<Rental> rentals, ArrayList<Customer> customers, ArrayList<Vehicle> vehicles,
            ArrayList<MaintenanceRecord> maintenanceRecords, DataManager dataManager) {
        // oi 4 listes einai ta vasika in-memory "pinakia" pou syntonizei o manager gia ta business rules
        this.rentals = rentals;
        this.customers = customers;
        this.vehicles = vehicles;
        this.maintenanceRecords = maintenanceRecords;
        this.dataManager = dataManager;
    }

    // vriskoume exact matches stin idia kathgoria
    public ArrayList<Vehicle> findExactAvailableVehicles(String requestedCategory, int requiredSeats, LocalDate startDate,
            int durationDays) {
        validateRentalRequest(startDate, durationDays, requiredSeats);

        ArrayList<Vehicle> matches = new ArrayList<>();
        // metatrepoume to duration se endDate gia na xrhsimopoioume to koino overlap model tou project
        LocalDate endDate = startDate.plusDays(durationDays);

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getCategoryName().equalsIgnoreCase(requestedCategory)
                    && vehicle.getSeats() >= requiredSeats
                    && vehicle.isAvailable(startDate, endDate, rentals, maintenanceRecords)) {
                matches.add(vehicle);
            }
        }

        // edw to Comparator deixnei kathara oti oi protaseis taxinomountai apo to pio fthino sto pio akrivo
        sortVehiclesByPrice(matches);
        return matches;
    }

    // vriskoume alternatives pio akrives i idies se seats
    // edw akolouthoume ton kanona tou assignment gia pio akrives epiloges an den yparxei exact match
    public ArrayList<Vehicle> findAlternativeVehicles(String requestedCategory, int requiredSeats, LocalDate startDate,
            int durationDays) {
        validateRentalRequest(startDate, durationDays, requiredSeats);

        ArrayList<Vehicle> matches = new ArrayList<>();
        LocalDate endDate = startDate.plusDays(durationDays);
        double requestedRate = getCategoryRate(requestedCategory);

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getSeats() >= requiredSeats
                    && vehicle.getDailyRate() > requestedRate
                    && vehicle.isAvailable(startDate, endDate, rentals, maintenanceRecords)) {
                matches.add(vehicle);
            }
        }

        // kai stis alternatives kratame tin idia seira gia na einai predictable sto demo
        sortVehiclesByPrice(matches);
        return matches;
    }

    // preview rental prin ginei to actual create
    public Rental createPreview(Customer customer, Vehicle vehicle, LocalDate startDate, int durationDays,
            String requestedCategory, int requiredSeats, ArrayList<ExtraService> extraServices, int redeemedPoints) {
        // preview object gia na deixoume invoice prin ginei to teliko save
        return new Rental("PREVIEW", customer, vehicle, startDate, durationDays, requestedCategory, requiredSeats,
                extraServices, redeemedPoints);
    }

    // dimiourgia rental me olo to validation kai save
    public Rental createRental(Customer customer, Vehicle vehicle, LocalDate startDate, int durationDays,
            String requestedCategory, int requiredSeats, ArrayList<ExtraService> extraServices, int redeemedPoints)
            throws NoAvailableVehicleException {
        validateRentalRequest(startDate, durationDays, requiredSeats);

        LocalDate endDate = startDate.plusDays(durationDays);

        if (!vehicle.isAvailable(startDate, endDate, rentals, maintenanceRecords)) {
            throw new NoAvailableVehicleException("To epilegmeno oxhma den einai pleon diathesimo.");
        }

        // ftiaxnoume prwta preview gia na ypologisoume me asfaleia to max loyalty redeem prin to teliko create
        Rental preview = createPreview(customer, vehicle, startDate, durationDays, requestedCategory, requiredSeats,
                extraServices, 0);
        int maxRedeemable = customer.getLoyaltyAccount().calculateMaxRedeemablePoints(preview.getTotalBeforeDiscount());

        if (redeemedPoints > maxRedeemable) {
            throw new IllegalArgumentException("Ta loyalty points pou dothikan einai parapano apo to epitrepto orio.");
        }

        // edw ginetai loyalty redemption prin to save tis synallagis
        customer.getLoyaltyAccount().redeemPoints(redeemedPoints);

        Rental rental = new Rental(IdGenerator.generateRentalId(rentals), customer, vehicle, startDate, durationDays,
                requestedCategory, requiredSeats, extraServices, redeemedPoints);
        rentals.add(rental);

        // file persistence: save amesws meta apo kathe allagh
        dataManager.saveCustomers(customers);
        dataManager.saveRentals(rentals);
        return rental;
    }

    // oloklirwsi rental me enimerwsi km, loyalty kai maintenance
    public MaintenanceRecord completeRental(String rentalId, int kilometersDriven, LocalDate completionDate,
            MaintenanceManager maintenanceManager) {
        Rental rental = findByIdOrNull(rentalId);

        if (rental == null) {
            throw new IllegalArgumentException("Den vrethike enoikiasi me ayto to ID.");
        }

        if (rental.getStatus() == RentalStatus.COMPLETED) {
            throw new IllegalStateException("I enoikiasi exei idi oloklirwthei.");
        }

        if (completionDate.isBefore(rental.getEndDate())) {
            throw new IllegalStateException(
                    "H oloklirwsi ginetai stin scheduled return date i meta apo auth, wste na einai synexes to history.");
        }

        if (kilometersDriven <= 0) {
            throw new IllegalArgumentException("Ta kilometers pou tha prostethoun prepei na einai thetika.");
        }

        // bonus logic: otan kleinei to rental, tote monon prostithentai km kai loyalty points
        rental.setStatus(RentalStatus.COMPLETED);
        rental.getVehicle().addKilometers(kilometersDriven);
        rental.getCustomer().getLoyaltyAccount().addPointsFromPayment(rental.getFinalPaid());

        MaintenanceRecord newRecord = null;

        if (rental.getVehicle().getTotalKilometers() >= rental.getVehicle().getNextMaintenanceThreshold()) {
            // bonus logic: apo to completion kai meta, an perase to threshold, schedularei maintenance
            LocalDate baseDate = completionDate;
            newRecord = maintenanceManager.scheduleMaintenance(
                    rental.getVehicle(),
                    baseDate,
                    "Automatic maintenance meta apo oloklirwsi enoikiasis " + rental.getRentalId());

            // an to oxhma perase to current threshold, pame sto epomeno 10.000
            while (rental.getVehicle().getNextMaintenanceThreshold() <= rental.getVehicle().getTotalKilometers()) {
                rental.getVehicle().setNextMaintenanceThreshold(rental.getVehicle().getNextMaintenanceThreshold() + 10000);
            }
        }

        // file persistence: save ta panta amesws meta tin oloklirwsi
        dataManager.saveVehicles(vehicles);
        dataManager.saveCustomers(customers);
        dataManager.saveRentals(rentals);
        return newRecord;
    }

    // anazitisi rental me id
    public Rental findById(String rentalId) {
        Rental rental = findByIdOrNull(rentalId);
        if (rental == null) {
            throw new IllegalArgumentException("Den vrethike enoikiasi me ID: " + rentalId);
        }
        return rental;
    }

    // helper methodos me null
    public Rental findByIdOrNull(String rentalId) {
        for (Rental rental : rentals) {
            if (rental.getRentalId().equalsIgnoreCase(rentalId)) {
                return rental;
            }
        }
        return null;
    }

    // rental history ana pelati
    public ArrayList<Rental> getRentalsForCustomer(String customerId) {
        ArrayList<Rental> results = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getCustomer().getCustomerId().equalsIgnoreCase(customerId)) {
                results.add(rental);
            }
        }

        results.sort(Comparator.comparing(Rental::getStartDate));
        return results;
    }

    // rental history ana oxhma
    public ArrayList<Rental> getRentalsForVehicle(String registrationNumber) {
        ArrayList<Rental> results = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
                results.add(rental);
            }
        }

        results.sort(Comparator.comparing(Rental::getStartDate));
        return results;
    }

    // rentals pou sygkrouontai me sygkekrimeno availability check
    public ArrayList<Rental> getBlockingRentalsForVehicle(String registrationNumber, LocalDate startDate,
            LocalDate endDate) {
        ArrayList<Rental> results = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)
                    && rental.overlaps(startDate, endDate)) {
                results.add(rental);
            }
        }

        return results;
    }

    // trexousa active rental pelati an yparxei simera
    public Rental getCurrentActiveRentalForCustomer(String customerId, LocalDate today) {
        for (Rental rental : rentals) {
            if (rental.getCustomer().getCustomerId().equalsIgnoreCase(customerId)
                    && rental.getStatus() == RentalStatus.ACTIVE
                    && !today.isBefore(rental.getStartDate())
                    && today.isBefore(rental.getEndDate())) {
                return rental;
            }
        }
        return null;
    }

    // lista me active rentals gia menu completion
    public ArrayList<Rental> getActiveRentals() {
        ArrayList<Rental> active = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getStatus() == RentalStatus.ACTIVE) {
                active.add(rental);
            }
        }

        active.sort(Comparator.comparing(Rental::getStartDate));
        return active;
    }

    // lista me active rentals pou mporoun ontws na oloklirwthoun simera
    // auto einai pio katharo sto menu apo to na deixnoume rentals pou den eftase akoma i imera epistrofis
    public ArrayList<Rental> getCompletableActiveRentals(LocalDate referenceDate) {
        ArrayList<Rental> results = new ArrayList<>();

        for (Rental rental : rentals) {
            if (rental.getStatus() == RentalStatus.ACTIVE && !referenceDate.isBefore(rental.getEndDate())) {
                results.add(rental);
            }
        }

        results.sort(Comparator.comparing(Rental::getEndDate).thenComparing(Rental::getRentalId));
        return results;
    }

    // helper gia requested category rate
    private double getCategoryRate(String category) {
        // auto einai mikro lookup table pou mas dinei to base rate tis kathgorias pou zitisthike
        if ("EconomyCar".equalsIgnoreCase(category)) {
            return 45.0;
        }
        if ("SUV".equalsIgnoreCase(category)) {
            return 70.0;
        }
        if ("Convertible".equalsIgnoreCase(category)) {
            return 90.0;
        }
        if ("LuxuryCar".equalsIgnoreCase(category)) {
            return 120.0;
        }
        throw new IllegalArgumentException("Agnwsti kathgoria oxhmatos.");
    }

    public ArrayList<Rental> getRentals() {
        return rentals;
    }

    // helper validation gia new rental request
    private void validateRentalRequest(LocalDate startDate, int durationDays, int requiredSeats) {
        if (startDate == null) {
            throw new IllegalArgumentException("To start date den prepei na einai null.");
        }

        if (durationDays <= 0) {
            throw new IllegalArgumentException("To duration prepei na einai thetiko.");
        }

        if (requiredSeats <= 0) {
            throw new IllegalArgumentException("Oi required seats prepei na einai thetikes.");
        }
    }

    // helper sorting methodos gia na min antigrafoume ton idio comparator
    private void sortVehiclesByPrice(ArrayList<Vehicle> vehiclesToSort) {
        // se isovathmia kratame registration number gia predictable kai statheri seira emfanisis
        vehiclesToSort.sort(Comparator.comparingDouble(Vehicle::getDailyRate)
                .thenComparing(Vehicle::getRegistrationNumber));
    }
}
