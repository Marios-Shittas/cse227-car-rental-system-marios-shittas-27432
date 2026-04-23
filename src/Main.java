// Όνομα: Μάριος Σιήττας
// Α.Φ.Τ.: 27432
// Διδάσκων: κ. Παναγιώτης Ηλία

import java.time.LocalDate;
import java.util.ArrayList;

// kyria klasi tou console application
// edw kratame to menu kai sundeoume ta managers gia na leitourgei olo to systima
public class Main {

    // helper gia asfales console input
    private InputHelper inputHelper;

    // managers pou xwrizoun ti logiki se mikra kommatia
    private DataManager dataManager;
    private VehicleManager vehicleManager;
    private CustomerManager customerManager;
    private RentalManager rentalManager;
    private MaintenanceManager maintenanceManager;

    // constructor pou kanei auto-load apo ta arxeia stin ekkinisi
    public Main() {
        inputHelper = new InputHelper();
        dataManager = new DataManager();

        // file persistence: fortwnoume prwta ta base entities kai meta syndeoume rentals/maintenance me references
        ArrayList<Vehicle> vehicles = dataManager.loadVehicles();
        ArrayList<Customer> customers = dataManager.loadCustomers();
        ArrayList<Rental> rentals = dataManager.loadRentals(customers, vehicles);
        ArrayList<MaintenanceRecord> maintenanceRecords = dataManager.loadMaintenanceRecords(vehicles);
        // autoi oi 4 ArrayLists leitourgoun san oi vasikoi "pinakes" dedomenwn oso trexei to programma

        vehicleManager = new VehicleManager(vehicles, dataManager);
        customerManager = new CustomerManager(customers, dataManager);
        maintenanceManager = new MaintenanceManager(maintenanceRecords, dataManager);
        rentalManager = new RentalManager(rentals, customers, vehicles, maintenanceRecords, dataManager);

        // refresh maintenance flags me basi to simerino date
        maintenanceManager.refreshCompletionStatuses(LocalDate.now());
    }

    // main method pou ksekina to programma
    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    // central loop tou menu
    public void run() {
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = inputHelper.readMenuChoice("Choose option: ", 0, 9);

            try {
                // to switch kanei routing apo to menu stin antistoixi epixeirisiaki roi
                switch (choice) {
                    case 1:
                        vehicleManagementMenu();
                        break;
                    case 2:
                        customerManagementMenu();
                        break;
                    case 3:
                        createRentalFlow();
                        break;
                    case 4:
                        printInvoiceFlow();
                        break;
                    case 5:
                        showVehicleAvailabilityFlow();
                        break;
                    case 6:
                        showCustomerHistoryFlow();
                        break;
                    case 7:
                        showVehicleHistoryFlow();
                        break;
                    case 8:
                        printAllData();
                        break;
                    case 9:
                        completeRentalFlow();
                        break;
                    case 0:
                        running = false;
                        break;
                    default:
                        break;
                }
            } catch (Exception exception) {
                // exception handling sto main loop gia na min "peftei" olo to programma
                System.out.println("Provlima: " + exception.getMessage());
            }
        }

        inputHelper.close();
        System.out.println("To programma termatise kanonika.");
    }

    // ektypwsi main menu opws zitithike
    private void printMainMenu() {
        System.out.println();
        System.out.println("Car Rental Management System");
        System.out.println("1. Vehicle Management");
        System.out.println("2. Customer Management");
        System.out.println("3. Create Rental");
        System.out.println("4. Print Invoice");
        System.out.println("5. Show Vehicle Availability");
        System.out.println("6. Show Customer History");
        System.out.println("7. Show Vehicle History");
        System.out.println("8. Print All Data");
        System.out.println("9. Complete Rental");
        System.out.println("0. Exit");
    }

    // submenu gia vehicles
    private void vehicleManagementMenu() {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("Vehicle Management");
            System.out.println("1. Add vehicle");
            System.out.println("2. Delete vehicle");
            System.out.println("3. Print all vehicles");
            System.out.println("0. Back");

            int choice = inputHelper.readMenuChoice("Choose option: ", 0, 3);

            switch (choice) {
                case 1:
                    addVehicleFlow();
                    break;
                case 2:
                    deleteVehicleFlow();
                    break;
                case 3:
                    printVehicles();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    // submenu gia customers
    private void customerManagementMenu() {
        boolean back = false;

        while (!back) {
            System.out.println();
            System.out.println("Customer Management");
            System.out.println("1. Register customer");
            System.out.println("2. Search customer");
            System.out.println("3. Print all customers");
            System.out.println("0. Back");

            int choice = inputHelper.readMenuChoice("Choose option: ", 0, 3);

            switch (choice) {
                case 1:
                    registerCustomerFlow();
                    break;
                case 2:
                    searchCustomerFlow();
                    break;
                case 3:
                    printCustomers();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    // roi gia prosthiki oxhmatos
    private void addVehicleFlow() {
        try {
            String category = chooseVehicleCategory();
            String registration = inputHelper.readNonEmptyString("Registration number: ");
            String brand = inputHelper.readNonEmptyString("Brand: ");
            String model = inputHelper.readNonEmptyString("Model: ");
            int year = inputHelper.readPositiveInt("Year: ");
            int seats = inputHelper.readPositiveInt("Seats: ");
            int totalKilometers = inputHelper.readPositiveInt("Total kilometers: ");
            int nextMaintenanceThreshold = inputHelper.readPositiveInt("Next maintenance threshold: ");

            Vehicle vehicle = createVehicleByCategory(category, registration, brand, model, year, seats,
                    totalKilometers, nextMaintenanceThreshold);

            vehicleManager.addVehicle(vehicle);
            System.out.println("To oxhma prostethike epituxws.");
        } catch (Exception exception) {
            // exception handling me filiko minima gia fitites kai demo
            System.out.println("Den egine prosthiki oxhmatos: " + exception.getMessage());
        }
    }

    // helper factory sto UI meros
    private Vehicle createVehicleByCategory(String category, String registration, String brand, String model, int year,
            int seats, int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        // edw fainetai polymorphism giati epistrefoume Vehicle reference enw dimiourgoume sygkekrimenes ypoklaseis
        if ("EconomyCar".equalsIgnoreCase(category)) {
            return new EconomyCar(registration, brand, model, year, seats, totalKilometers, nextMaintenanceThreshold);
        }
        if ("SUV".equalsIgnoreCase(category)) {
            return new SUV(registration, brand, model, year, seats, totalKilometers, nextMaintenanceThreshold);
        }
        if ("Convertible".equalsIgnoreCase(category)) {
            return new Convertible(registration, brand, model, year, seats, totalKilometers, nextMaintenanceThreshold);
        }
        if ("LuxuryCar".equalsIgnoreCase(category)) {
            return new LuxuryCar(registration, brand, model, year, seats, totalKilometers, nextMaintenanceThreshold);
        }
        throw new IllegalArgumentException("Agnwsti kathgoria.");
    }

    // roi gia diagrafi oxhmatos
    private void deleteVehicleFlow() {
        try {
            String registration = inputHelper.readNonEmptyString("Registration number to delete: ");
            vehicleManager.deleteVehicle(registration, rentalManager.getRentals(), maintenanceManager.getMaintenanceRecords());
            System.out.println("To oxhma diagrafike epituxws.");
        } catch (Exception exception) {
            System.out.println("Den egine diagrafi oxhmatos: " + exception.getMessage());
        }
    }

    // roi gia eggrafi pelati
    private void registerCustomerFlow() {
        try {
            String fullName = inputHelper.readNonEmptyString("Full name: ");
            String identityCard = inputHelper.readNonEmptyString("Identity card number: ");
            String address = inputHelper.readNonEmptyString("Address: ");
            String phone = inputHelper.readNonEmptyString("Phone number: ");
            String email = inputHelper.readNonEmptyString("Email: ");
            String creditCard = inputHelper.readNonEmptyString("Credit card number: ");

            Customer customer = customerManager.registerCustomer(fullName, identityCard, address, phone, email,
                    creditCard);
            System.out.println("O pelatis egine register epituxws.");
            System.out.println(customer);
        } catch (Exception exception) {
            System.out.println("Den egine register pelati: " + exception.getMessage());
        }
    }

    // roi gia search customer apo submenu
    private void searchCustomerFlow() {
        try {
            Customer customer = findCustomerFromPrompt();
            printCustomerFullInfo(customer);
        } catch (Exception exception) {
            System.out.println("Den egine anazitisi pelati: " + exception.getMessage());
        }
    }

    // roi gia dimiourgia rental
    private void createRentalFlow() {
        try {
            Customer customer = findCustomerFromPrompt();
            LocalDate startDate = inputHelper.readDateNotBefore("Start date (yyyy-MM-dd): ", LocalDate.now());
            int durationDays = inputHelper.readPositiveInt("Duration in days: ");
            String requestedCategory = chooseVehicleCategory();
            int requiredSeats = inputHelper.readPositiveInt("Required seats: ");
            ArrayList<ExtraService> extraServices = chooseExtraServices();

            ArrayList<Vehicle> exactMatches = rentalManager.findExactAvailableVehicles(requestedCategory, requiredSeats,
                    startDate, durationDays);

            // to selectableVehicles einai o telikos "pinakas" epilogwn pou tha deixoume ston admin
            ArrayList<Vehicle> selectableVehicles;
            if (!exactMatches.isEmpty()) {
                System.out.println("Brethikan exact matches stin zhtoumeni kathgoria:");
                selectableVehicles = exactMatches;
            } else {
                selectableVehicles = rentalManager.findAlternativeVehicles(requestedCategory, requiredSeats, startDate,
                        durationDays);

                if (selectableVehicles.isEmpty()) {
                    throw new NoAvailableVehicleException(
                            "Den brethike oute exact match oute pio akrivi enallaktiki lysh me arketa seats.");
                }

                System.out.println("Den yparxei exact match. Emfanizontai diathesimes pio akrives enallaktikes:");
            }

            Vehicle selectedVehicle = chooseVehicleFromList(selectableVehicles);
            if (selectedVehicle == null) {
                System.out.println("Akyrosi dimiourgias rental.");
                return;
            }

            // to preview invoice deixnei ston admin ti tha plirwsei prin to teliko save
            Rental preview = rentalManager.createPreview(customer, selectedVehicle, startDate, durationDays,
                    requestedCategory, requiredSeats, extraServices, 0);

            System.out.println(preview.getInvoiceText());

            int redeemedPoints = chooseRedeemedPoints(customer, preview.getTotalBeforeDiscount());
            Rental rental = rentalManager.createRental(customer, selectedVehicle, startDate, durationDays,
                    requestedCategory, requiredSeats, extraServices, redeemedPoints);

            System.out.println("I enoikiasi dimiourgithike epituxws.");
            System.out.println(rental.getInvoiceText());
        } catch (Exception exception) {
            System.out.println("Den egine dimiourgia rental: " + exception.getMessage());
        }
    }

    // reprint invoice
    private void printInvoiceFlow() {
        try {
            String rentalId = inputHelper.readNonEmptyString("Rental ID: ");
            Rental rental = rentalManager.findById(rentalId);
            // invoice re-print me vasi to Rental ID opws zita to assignment
            System.out.println(rental.getInvoiceText());
        } catch (Exception exception) {
            System.out.println("Den egine ektypwsi invoice: " + exception.getMessage());
        }
    }

    // roi gia emfanisi availability
    private void showVehicleAvailabilityFlow() {
        try {
            System.out.println("1. Specific date");
            System.out.println("2. Date range");
            int choice = inputHelper.readMenuChoice("Choose option: ", 1, 2);

            LocalDate startDate;
            LocalDate endDate;

            if (choice == 1) {
                startDate = inputHelper.readDate("Date (yyyy-MM-dd): ");
                endDate = startDate.plusDays(1);
            } else {
                startDate = inputHelper.readDate("Start date (yyyy-MM-dd): ");
                LocalDate endInclusive = inputHelper.readDate("End date inclusive (yyyy-MM-dd): ");
                if (endInclusive.isBefore(startDate)) {
                    System.out.println("To telos den mporei na einai prin tin arxi.");
                    return;
                }
                endDate = endInclusive.plusDays(1);
            }

            // edw kratame panta to idio overlap convention: start inclusive, end exclusive
            printAvailabilityForRange(startDate, endDate);
        } catch (Exception exception) {
            System.out.println("Den egine elegxos availability: " + exception.getMessage());
        }
    }

    // roi gia customer history
    private void showCustomerHistoryFlow() {
        try {
            Customer customer = findCustomerFromPrompt();
            printCustomerFullInfo(customer);
        } catch (Exception exception) {
            System.out.println("Den egine emfanisi customer history: " + exception.getMessage());
        }
    }

    // roi gia vehicle history
    private void showVehicleHistoryFlow() {
        try {
            String registration = inputHelper.readNonEmptyString("Vehicle registration number: ");
            Vehicle vehicle = vehicleManager.findByRegistration(registration);

            System.out.println();
            System.out.println("Vehicle Details");
            System.out.println(vehicle);

            LocalDate today = LocalDate.now();
            boolean availableToday = vehicle.isAvailable(today, today.plusDays(1), rentalManager.getRentals(),
                    maintenanceManager.getMaintenanceRecords());

            // edw kanoume mikro availability check mias imeras gia grigoro current status sto history screen
            System.out.println("Current Status Today: " + (availableToday ? "AVAILABLE" : "UNAVAILABLE"));

            ArrayList<Rental> rentals = rentalManager.getRentalsForVehicle(registration);
            ArrayList<MaintenanceRecord> records = maintenanceManager.getRecordsForVehicle(registration);

            System.out.println();
            System.out.println("Rental History");
            if (rentals.isEmpty()) {
                System.out.println("Den yparxei rental history.");
            } else {
                for (Rental rental : rentals) {
                    System.out.println(rental);
                }
            }

            System.out.println();
            System.out.println("Maintenance History");
            if (records.isEmpty()) {
                System.out.println("Den yparxei maintenance history.");
            } else {
                for (MaintenanceRecord record : records) {
                    System.out.println(record);
                }
            }
        } catch (Exception exception) {
            System.out.println("Den egine emfanisi vehicle history: " + exception.getMessage());
        }
    }

    // ektypwsi olwn twn dedomenwn
    private void printAllData() {
        System.out.println();
        System.out.println("========== ALL VEHICLES ==========");
        printVehicles();

        System.out.println();
        System.out.println("========== ALL CUSTOMERS ==========");
        printCustomers();

        System.out.println();
        System.out.println("========== ALL RENTALS ==========");
        printRentals();

        System.out.println();
        System.out.println("========== ALL MAINTENANCE RECORDS ==========");
        printMaintenanceRecords();
    }

    // roi gia complete rental
    private void completeRentalFlow() {
        try {
            ArrayList<Rental> activeRentals = rentalManager.getCompletableActiveRentals(LocalDate.now());

            if (activeRentals.isEmpty()) {
                System.out.println("Den yparxoun active rentals pou eftasan stin imera epistrofis tous.");
                return;
            }

            System.out.println("Completable Active Rentals:");
            for (Rental rental : activeRentals) {
                System.out.println(rental);
            }

            String rentalId = inputHelper.readNonEmptyString("Rental ID to complete: ");
            Rental rental = rentalManager.findById(rentalId);

            System.out.println("1. Estimated kilometers (125 km/day)");
            System.out.println("2. Custom kilometers");
            int choice = inputHelper.readMenuChoice("Choose option: ", 1, 2);

            int kilometersDriven;
            if (choice == 1) {
                kilometersDriven = rental.getDurationDays() * 125;
            } else {
                kilometersDriven = inputHelper.readPositiveInt("Custom kilometers: ");
            }

            MaintenanceRecord maintenanceRecord = rentalManager.completeRental(rentalId, kilometersDriven,
                    LocalDate.now(), maintenanceManager);

            System.out.println("I enoikiasi oloklirwthike epituxws.");
            System.out.println("Earned loyalty points: " + rental.calculateEarnedLoyaltyPoints());
            System.out.println("Vehicle new total kilometers: " + rental.getVehicle().getTotalKilometers());

            if (maintenanceRecord != null) {
                System.out.println("Dimiourgithike auto maintenance:");
                System.out.println(maintenanceRecord);
            }
        } catch (Exception exception) {
            System.out.println("Den egine oloklirwsi rental: " + exception.getMessage());
        }
    }

    // helper gia anazitisi customer me 2 tropous
    private Customer findCustomerFromPrompt() throws CustomerNotFoundException {
        System.out.println("1. Search by identity card");
        System.out.println("2. Search by loyalty card");
        int choice = inputHelper.readMenuChoice("Choose option: ", 1, 2);

        if (choice == 1) {
            String identityCard = inputHelper.readNonEmptyString("Identity card number: ");
            return customerManager.findByIdentityCard(identityCard);
        }

        String loyaltyCard = inputHelper.readNonEmptyString("Loyalty card number: ");
        return customerManager.findByLoyaltyCard(loyaltyCard);
    }

    // helper gia epilogi category
    private String chooseVehicleCategory() {
        System.out.println("1. EconomyCar");
        System.out.println("2. SUV");
        System.out.println("3. Convertible");
        System.out.println("4. LuxuryCar");

        int choice = inputHelper.readMenuChoice("Choose category: ", 1, 4);

        switch (choice) {
            case 1:
                return "EconomyCar";
            case 2:
                return "SUV";
            case 3:
                return "Convertible";
            case 4:
                return "LuxuryCar";
            default:
                throw new IllegalArgumentException("Agnwsti kathgoria.");
        }
    }

    // helper gia epilogi extra services
    private ArrayList<ExtraService> chooseExtraServices() {
        // o proswrinos autos "pinakas" extras pernaei meta stin Rental ws snapshot twn epilogwn
        ArrayList<ExtraService> services = new ArrayList<>();

        if (inputHelper.readYesNo("Add GPS? (yes/no): ")) {
            services.add(ExtraService.GPS);
        }
        if (inputHelper.readYesNo("Add Child Seat? (yes/no): ")) {
            services.add(ExtraService.CHILD_SEAT);
        }
        if (inputHelper.readYesNo("Add Additional Insurance? (yes/no): ")) {
            services.add(ExtraService.ADDITIONAL_INSURANCE);
        }

        return services;
    }

    // helper gia epilogi oxhmatos apo lista
    // edw o admin vlepei taksinomimena ta diathesima oxhmata kai apofasizei
    private Vehicle chooseVehicleFromList(ArrayList<Vehicle> vehicles) {
        // i lista erxetai idi taxinomimeni apo ton RentalManager kai edw aplws ginetai i teliki epilogi
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.println((i + 1) + ". " + vehicles.get(i));
        }
        System.out.println("0. Cancel");

        int choice = inputHelper.readMenuChoice("Choose vehicle: ", 0, vehicles.size());
        if (choice == 0) {
            return null;
        }

        return vehicles.get(choice - 1);
    }

    // helper gia loyalty redemption
    private int chooseRedeemedPoints(Customer customer, double totalCost) {
        LoyaltyAccount account = customer.getLoyaltyAccount();
        // to totalCost einai prin to discount, opote pano se auto ypologizetai to epitrepto redeem
        int maxRedeemable = account.calculateMaxRedeemablePoints(totalCost);

        if (!account.canRedeem() || maxRedeemable == 0) {
            System.out.println("Den yparxoun diathesimoi points gia redeem.");
            return 0;
        }

        System.out.println("Customer loyalty points: " + account.getPoints());
        System.out.println("Max redeemable points: " + maxRedeemable);

        if (!inputHelper.readYesNo("Do you want to redeem points? (yes/no): ")) {
            return 0;
        }

        while (true) {
            int points = inputHelper.readInt("Points to redeem (multiple of 10, 0 to cancel): ");

            if (points == 0) {
                return 0;
            }

            if (points >= 50 && points <= maxRedeemable && points % 10 == 0) {
                return points;
            }

            System.out.println("Mi egkyro redeem. Prepei na einai 50+ , pollaplasio tou 10 kai mexri "
                    + maxRedeemable + ".");
        }
    }

    // emfanisi full info pelati mazi me history kai current rental
    private void printCustomerFullInfo(Customer customer) {
        System.out.println();
        System.out.println(customer);

        // auto to history einai filtrarismenos "pinakas" rentals mono gia ton sygkekrimeno pelati
        ArrayList<Rental> history = rentalManager.getRentalsForCustomer(customer.getCustomerId());

        System.out.println();
        System.out.println("Rental History");
        if (history.isEmpty()) {
            System.out.println("Den yparxei rental history.");
        } else {
            for (Rental rental : history) {
                System.out.println(rental);
            }
        }

        Rental activeRental = rentalManager.getCurrentActiveRentalForCustomer(customer.getCustomerId(), LocalDate.now());
        if (activeRental != null) {
            System.out.println();
            System.out.println("Current Active Rental Invoice");
            System.out.println(activeRental.getInvoiceText());
        }
    }

    // ektypwsi availability me available/unavailable sections kai reasons
    private void printAvailabilityForRange(LocalDate startDate, LocalDate endDate) {
        DateUtils.validateDateRange(startDate, endDate, "Availability range");

        System.out.println();
        System.out.println("Availability from " + startDate + " to " + endDate + " (end exclusive)");

        System.out.println();
        System.out.println("AVAILABLE VEHICLES");
        boolean foundAvailable = false;

        // prwta deixnoume ton "pinaka" available kai meta ton "pinaka" unavailable gia pio katharo output
        for (Vehicle vehicle : vehicleManager.getVehicles()) {
            if (vehicle.isAvailable(startDate, endDate, rentalManager.getRentals(),
                    maintenanceManager.getMaintenanceRecords())) {
                foundAvailable = true;
                System.out.println(vehicle);
            }
        }

        if (!foundAvailable) {
            System.out.println("Den yparxoun diathesima oxhmata.");
        }

        System.out.println();
        System.out.println("UNAVAILABLE VEHICLES");
        boolean foundUnavailable = false;

        for (Vehicle vehicle : vehicleManager.getVehicles()) {
            if (!vehicle.isAvailable(startDate, endDate, rentalManager.getRentals(),
                    maintenanceManager.getMaintenanceRecords())) {
                foundUnavailable = true;
                System.out.println(vehicle);

                // edw deixnoume giati ena oxhma den einai available: rental overlap i maintenance overlap
                ArrayList<Rental> blockingRentals = rentalManager.getBlockingRentalsForVehicle(
                        vehicle.getRegistrationNumber(), startDate, endDate);
                ArrayList<MaintenanceRecord> blockingMaintenance = maintenanceManager.getBlockingRecordsForVehicle(
                        vehicle.getRegistrationNumber(), startDate, endDate);

                for (Rental rental : blockingRentals) {
                    System.out.println("  Blocking rental: " + rental);
                }
                for (MaintenanceRecord record : blockingMaintenance) {
                    System.out.println("  Blocking maintenance: " + record);
                }
            }
        }

        if (!foundUnavailable) {
            System.out.println("Den yparxoun unavailable oxhmata sto sygkekrimeno diasthma.");
        }
    }

    // ektypwsi vehicles
    private void printVehicles() {
        if (vehicleManager.getVehicles().isEmpty()) {
            System.out.println("Den yparxoun oxhmata.");
            return;
        }

        for (Vehicle vehicle : vehicleManager.getVehicles()) {
            System.out.println(vehicle);
        }
    }

    // ektypwsi customers
    private void printCustomers() {
        if (customerManager.getCustomers().isEmpty()) {
            System.out.println("Den yparxoun pelates.");
            return;
        }

        for (Customer customer : customerManager.getCustomers()) {
            System.out.println(customer);
            System.out.println("------------------------------");
        }
    }

    // ektypwsi rentals
    private void printRentals() {
        if (rentalManager.getRentals().isEmpty()) {
            System.out.println("Den yparxoun rentals.");
            return;
        }

        for (Rental rental : rentalManager.getRentals()) {
            System.out.println(rental);
        }
    }

    // ektypwsi maintenance
    private void printMaintenanceRecords() {
        if (maintenanceManager.getMaintenanceRecords().isEmpty()) {
            System.out.println("Den yparxoun maintenance records.");
            return;
        }

        for (MaintenanceRecord record : maintenanceManager.getMaintenanceRecords()) {
            System.out.println(record);
        }
    }
}
