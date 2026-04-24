import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// klasi gia file persistence
// edw ginetai to fortwma kai to save se txt files wste to programma na synexizei meta apo restart
public class DataManager {

    // relative paths opws zitithikan sto assignment
    private static final Path DATA_DIRECTORY = resolveDataDirectory();
    private static final String VEHICLES_FILE = DATA_DIRECTORY.resolve("vehicles.txt").toString();
    private static final String CUSTOMERS_FILE = DATA_DIRECTORY.resolve("customers.txt").toString();
    private static final String RENTALS_FILE = DATA_DIRECTORY.resolve("rentals.txt").toString();
    private static final String MAINTENANCE_FILE = DATA_DIRECTORY.resolve("maintenance.txt").toString();

    // constructor pou frontizei na yparxoun ta arxeia
    public DataManager() {
        ensureDataFilesExist();
    }

    // fortwma oxhmatwn
    public ArrayList<Vehicle> loadVehicles() {
        // auto to ArrayList leitourgei san o "pinakas" vehicles mesa sti mnimi meta to load
        ArrayList<Vehicle> vehicles = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(Paths.get(VEHICLES_FILE), StandardCharsets.UTF_8)) {
                // agnooume kena i sxolia gia na mporoun na yparxoun headers mesa sta txt files
                if (isIgnorableDataLine(line)) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length < 9) {
                    System.out.println("Paraleipetai invalid grammh sto vehicles.txt: " + line);
                    continue;
                }

                String type = parts[0];
                String registration = parts[1];
                String brand = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                int seats = Integer.parseInt(parts[5]);
                // to daily rate yparxei sto arxeio gia readability, alla h pragmatiki timi orizetai apo to subclass
                int totalKilometers = Integer.parseInt(parts[7]);
                int nextMaintenanceThreshold = Integer.parseInt(parts[8]);

                vehicles.add(createVehicle(type, registration, brand, model, year, seats, totalKilometers,
                        nextMaintenanceThreshold));
            }
        } catch (IOException exception) {
            // exception handling gia file operations opws zita to assignment
            System.out.println("Provlima sto fortwma twn oxhmatwn: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Geniko provlima sto parse twn oxhmatwn: " + exception.getMessage());
        }

        return vehicles;
    }

    // fortwma pelatwn
    public ArrayList<Customer> loadCustomers() {
        // kai edw xtizoume ton in-memory "pinaka" customers apo kathe egkyri grammh tou arxeiou
        ArrayList<Customer> customers = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(Paths.get(CUSTOMERS_FILE), StandardCharsets.UTF_8)) {
                if (isIgnorableDataLine(line)) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length < 9) {
                    System.out.println("Paraleipetai invalid grammh sto customers.txt: " + line);
                    continue;
                }

                LoyaltyAccount loyaltyAccount = new LoyaltyAccount(parts[7], Integer.parseInt(parts[8]));
                Customer customer = new Customer(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6],
                        loyaltyAccount);
                customers.add(customer);
            }
        } catch (IOException exception) {
            System.out.println("Provlima sto fortwma twn pelatwn: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Geniko provlima sto parse twn pelatwn: " + exception.getMessage());
        }

        return customers;
    }

    // fortwma enoikiasewn kai reconnect me customer/vehicle references
    // edw to file persistence den apothikeuei nested objects, alla ids opws zitithike
    public ArrayList<Rental> loadRentals(ArrayList<Customer> customers, ArrayList<Vehicle> vehicles) {
        // o "pinakas" rentals ftiaxnetai teleutaio giati xreiazetai na exoun idi fortwthei customers kai vehicles
        ArrayList<Rental> rentals = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(Paths.get(RENTALS_FILE), StandardCharsets.UTF_8)) {
                if (isIgnorableDataLine(line)) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length < 19) {
                    System.out.println("Paraleipetai invalid grammh sto rentals.txt: " + line);
                    continue;
                }

                Customer customer = findCustomerById(customers, parts[1]);
                Vehicle vehicle = findVehicleByRegistration(vehicles, parts[2]);

                if (customer == null || vehicle == null) {
                    System.out.println("Den egine reconnect rental record: " + line);
                    continue;
                }

                ArrayList<ExtraService> services = ExtraService.fromFlags(
                        Boolean.parseBoolean(parts[8]),
                        Boolean.parseBoolean(parts[9]),
                        Boolean.parseBoolean(parts[10]));

                Rental rental = new Rental(
                        parts[0],
                        customer,
                        vehicle,
                        LocalDate.parse(parts[3]),
                        LocalDate.parse(parts[4]),
                        Integer.parseInt(parts[5]),
                        parts[6],
                        Integer.parseInt(parts[7]),
                        services,
                        Double.parseDouble(parts[11]),
                        Double.parseDouble(parts[12]),
                        Double.parseDouble(parts[13]),
                        Double.parseDouble(parts[14]),
                        Double.parseDouble(parts[15]),
                        Double.parseDouble(parts[16]),
                        Integer.parseInt(parts[17]),
                        RentalStatus.valueOf(parts[18]));

                rentals.add(rental);
            }
        } catch (IOException exception) {
            System.out.println("Provlima sto fortwma twn enoikiasewn: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Geniko provlima sto parse twn enoikiasewn: " + exception.getMessage());
        }

        return rentals;
    }

    // fortwma maintenance records
    public ArrayList<MaintenanceRecord> loadMaintenanceRecords(ArrayList<Vehicle> vehicles) {
        // o maintenance "pinakas" syndeetai me idi yparxonta Vehicle objects anti gia antigrafa dedomenwn
        ArrayList<MaintenanceRecord> records = new ArrayList<>();

        try {
            for (String line : Files.readAllLines(Paths.get(MAINTENANCE_FILE), StandardCharsets.UTF_8)) {
                if (isIgnorableDataLine(line)) {
                    continue;
                }

                String[] parts = line.split("\\|", -1);
                if (parts.length < 7) {
                    System.out.println("Paraleipetai invalid grammh sto maintenance.txt: " + line);
                    continue;
                }

                Vehicle vehicle = findVehicleByRegistration(vehicles, parts[1]);
                if (vehicle == null) {
                    System.out.println("Den egine reconnect maintenance record: " + line);
                    continue;
                }

                MaintenanceRecord record = new MaintenanceRecord(
                        parts[0],
                        vehicle,
                        LocalDate.parse(parts[2]),
                        LocalDate.parse(parts[3]),
                        Integer.parseInt(parts[4]),
                        parts[5],
                        Boolean.parseBoolean(parts[6]));

                records.add(record);
            }
        } catch (IOException exception) {
            System.out.println("Provlima sto fortwma maintenance records: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Geniko provlima sto parse twn maintenance records: " + exception.getMessage());
        }

        return records;
    }

    // save oxhmatwn
    public void saveVehicles(List<Vehicle> vehicles) {
        ArrayList<String> lines = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            lines.add(vehicle.toDataString());
        }
        // file persistence: save amesws meta apo allagh gia na min xanontai dedomena meta apo restart
        writeLines(VEHICLES_FILE, lines);
    }

    // save pelatwn
    public void saveCustomers(List<Customer> customers) {
        ArrayList<String> lines = new ArrayList<>();
        for (Customer customer : customers) {
            lines.add(customer.toDataString());
        }
        writeLines(CUSTOMERS_FILE, lines);
    }

    // save enoikiasewn
    public void saveRentals(List<Rental> rentals) {
        ArrayList<String> lines = new ArrayList<>();
        for (Rental rental : rentals) {
            lines.add(rental.toDataString());
        }
        writeLines(RENTALS_FILE, lines);
    }

    // save maintenance
    public void saveMaintenanceRecords(List<MaintenanceRecord> records) {
        ArrayList<String> lines = new ArrayList<>();
        for (MaintenanceRecord record : records) {
            lines.add(record.toDataString());
        }
        writeLines(MAINTENANCE_FILE, lines);
    }

    // helper pou grafei se arxeio me exception handling
    private void writeLines(String filePath, List<String> lines) {
        try {
            // prin ginei to save, xanavaltoume ta sxolia-header gia na meinoun sta txt kai meta apo metrites allages
            Files.write(Paths.get(filePath), buildFileLinesWithHeader(filePath, lines), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            // exception handling sto write gia na min stamata to programma
            System.out.println("Provlima sto save tou arxeiou " + filePath + ": " + exception.getMessage());
        }
    }

    // helper gia dimiourgia arxeiwn an leipoun
    private void ensureDataFilesExist() {
        try {
            Files.createDirectories(DATA_DIRECTORY);
            createFileIfMissing(VEHICLES_FILE);
            createFileIfMissing(CUSTOMERS_FILE);
            createFileIfMissing(RENTALS_FILE);
            createFileIfMissing(MAINTENANCE_FILE);
        } catch (IOException exception) {
            System.out.println("Provlima sti dimiourgia twn data files: " + exception.getMessage());
        }
    }

    // helper pou vriskei to swsto data folder eite trexei apo root eite apo src
    private static Path resolveDataDirectory() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        Path directData = cwd.resolve("data");
        Path nestedData = cwd.resolve("CarRentalSystem").resolve("data");
        Path parentData = cwd.resolve("..").normalize().resolve("data");

        if ("src".equalsIgnoreCase(cwd.getFileName().toString())
                && Files.exists(parentData) && Files.isDirectory(parentData)) {
            return parentData;
        }
        if (Files.exists(directData) && Files.isDirectory(directData)) {
            return directData;
        }
        if (Files.exists(nestedData) && Files.isDirectory(nestedData)) {
            return nestedData;
        }
        if (Files.exists(parentData) && Files.isDirectory(parentData)) {
            return parentData;
        }

        return directData;
    }

    // helper gia ena arxeio
    private void createFileIfMissing(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createFile(path);
            // otan dimiourgeitai neo arxeio, grafoume amesws ta sxolia me tis stiles tou "pinaka"
            Files.write(path, getHeaderLines(filePath), StandardCharsets.UTF_8);
        }
    }

    // helper gia na agnooume sxolia kai kena stoixeia sta txt files
    private boolean isIgnorableDataLine(String line) {
        String trimmed = line.trim();
        return trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("#");
    }

    // helper pou sundiazei ta data rows me ta periexomena headers tou kathe arxeiou
    private List<String> buildFileLinesWithHeader(String filePath, List<String> dataLines) {
        ArrayList<String> linesWithHeader = new ArrayList<>(getHeaderLines(filePath));
        linesWithHeader.addAll(dataLines);
        return linesWithHeader;
    }

    // helper me perigraphi ton stilwn tou kathe txt arxeiou, san mikro schema tou "pinaka"
    private List<String> getHeaderLines(String filePath) {
        ArrayList<String> headerLines = new ArrayList<>();

        if (VEHICLES_FILE.equals(filePath)) {
            headerLines.add("// pseudo-pinakas vehicles: kathe grammh antistoixei se ena oxhma");
            headerLines.add("// stiles: type|registration|brand|model|year|seats|dailyRate|totalKilometers|nextMaintenanceThreshold");
        } else if (CUSTOMERS_FILE.equals(filePath)) {
            headerLines.add("// pseudo-pinakas customers: kathe grammh antistoixei se enan pelati");
            headerLines.add("// stiles: customerId|fullName|identityCard|address|phone|email|creditCard|loyaltyCard|points");
        } else if (RENTALS_FILE.equals(filePath)) {
            headerLines.add("// pseudo-pinakas rentals: kathe grammh einai snapshot mias enoikiasis");
            headerLines.add("// stiles: rentalId|customerId|vehicleRegistration|startDate|endDate|durationDays|requestedCategory|requiredSeats|gps|childSeat|insurance|baseCost|gpsCost|childSeatCost|insuranceCost|discount|finalPaid|redeemedPoints|status");
        } else if (MAINTENANCE_FILE.equals(filePath)) {
            headerLines.add("// pseudo-pinakas maintenance: kathe grammh antistoixei se ena maintenance record");
            headerLines.add("// stiles: maintenanceId|vehicleRegistration|startDate|endDate|kilometersAtService|description|completed");
        }

        headerLines.add("");
        return headerLines;
    }

    // factory methodos gia ta vehicles
    // edw h abstraction kai inheritance sundiazontai giati epistrefoume Vehicle reference
    private Vehicle createVehicle(String type, String registration, String brand, String model, int year, int seats,
            int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        switch (type.toLowerCase()) {
            case "economycar":
                return new EconomyCar(registration, brand, model, year, seats, totalKilometers,
                        nextMaintenanceThreshold);
            case "suv":
                return new SUV(registration, brand, model, year, seats, totalKilometers,
                        nextMaintenanceThreshold);
            case "convertible":
                return new Convertible(registration, brand, model, year, seats, totalKilometers,
                        nextMaintenanceThreshold);
            case "luxurycar":
                return new LuxuryCar(registration, brand, model, year, seats, totalKilometers,
                        nextMaintenanceThreshold);
            default:
                throw new IllegalArgumentException("Agnwstos typos oxhmatos: " + type);
        }
    }

    // helper gia reconnect customer
    private Customer findCustomerById(ArrayList<Customer> customers, String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        return null;
    }

    // helper gia reconnect vehicle
    private Vehicle findVehicleByRegistration(ArrayList<Vehicle> vehicles, String registrationNumber) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
                return vehicle;
            }
        }
        return null;
    }
}
