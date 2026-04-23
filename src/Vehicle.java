import java.time.LocalDate;
import java.util.List;

// abstract class gia na exoume koini vasi gia ola ta oxhmata
// edw deiknyoume abstraction giati kratame ta koinia stoixeia kai afhnoume stis ypoklaseis ta eidika
public abstract class Vehicle implements Bookable {

    // edw ginetai encapsulation afou ta fields einai private kai prosvasima mono me methods
    private String registrationNumber;

    // marka tou oxhmatos gia pio kalo readability stin ektypwsi
    private String brand;

    // montelo tou oxhmatos
    private String model;

    // etos kataskevis gia basic plirofories
    private int year;

    // plithos thesewn pou prepei na einai valid analoga me tin kathgoria
    private int seats;

    // stathero hmerhsio kostos analoga me ton typo
    private double dailyRate;

    // bonus meros: synolika xiliometra oxhmatos
    private int totalKilometers;

    // bonus meros: epomeno orio gia maintenance
    private int nextMaintenanceThreshold;

    // constructor pou gemizei ta koinia stoixeia gia ola ta oxhmata
    // xrhsimopoioume validateSeats edw gia na min dimiourgeitai pote akyro object
    public Vehicle(String registrationNumber, String brand, String model, int year, int seats,
            double dailyRate, int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        // edw ginontai basic validation checks gia na kratame swsta data apo tin arxi
        validateCommonData(registrationNumber, brand, model, year, totalKilometers, nextMaintenanceThreshold);

        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.brand = brand.trim();
        this.model = model.trim();
        this.year = year;
        this.dailyRate = dailyRate;
        this.totalKilometers = totalKilometers;
        this.nextMaintenanceThreshold = nextMaintenanceThreshold;
        validateSeats(seats);
        this.seats = seats;
    }

    // abstract methods gia na orizei kathe ypoklash to epitrepto euros thesewn
    protected abstract int getMinSeats();

    protected abstract int getMaxSeats();

    // methodos gia na pairnoume onoma kathgorias xwris extra field
    public String getCategoryName() {
        // etsi pairnoume dinamikotika to runtime type tou object kai oxi kapoio xeiropoihto string field
        return getClass().getSimpleName();
    }

    // edw ginetai polymorphism giati kathe subclass epistrefei diaforetika min/max seats
    private void validateSeats(int seats) throws InvalidSeatCountException {
        if (seats < getMinSeats() || seats > getMaxSeats()) {
            throw new InvalidSeatCountException(
                    "Mi egkyros arithmos thesewn gia " + getCategoryName() + ". Epitrepetai apo "
                            + getMinSeats() + " mexri " + getMaxSeats() + ".");
        }
    }

    // ylopoihsh tou interface method gia elegxo diathesimothtas
    // edw h diathesimothta lambanei ypopsi kai enoikiaseis kai maintenance periods
    @Override
    public boolean isAvailable(LocalDate startDate, LocalDate endDate, List<Rental> rentals,
            List<MaintenanceRecord> maintenanceRecords) {
        // edw fainetai ksana interface usage giati ola ta oxhmata exoun auto to booking behavior
        DateUtils.validateDateRange(startDate, endDate, "Vehicle availability check");

        for (Rental rental : rentals) {
            if (rental.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)
                    && rental.overlaps(startDate, endDate)) {
                // an vrethei overlap me enoikiasi, to oxhma den prepei na emfanistei ws available
                return false;
            }
        }

        for (MaintenanceRecord record : maintenanceRecords) {
            if (record.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)
                    && record.overlaps(startDate, endDate)) {
                // bonus logic: to maintenance blokarei kai auto diathesimothta
                return false;
            }
        }

        return true;
    }

    // methodos gia na au3anoume ta xiliometra otan oloklirwnetai mia enoikiasi
    public void addKilometers(int kilometers) {
        if (kilometers > 0) {
            totalKilometers += kilometers;
        }
    }

    // methodos gia file persistence se pipe-separated morfi
    public String toDataString() {
        // h seira twn pedion antistoixei me to schema tou vehicles.txt gia na xanadiavazetai swsta apo to DataManager
        return getCategoryName() + "|" + registrationNumber + "|" + brand + "|" + model + "|" + year + "|" + seats
                + "|" + DateUtils.formatMoney(dailyRate) + "|" + totalKilometers + "|" + nextMaintenanceThreshold;
    }

    // override tou Object.toString gia na exoume plires readable summary tou oxhmatos sta menus
    @Override
    public String toString() {
        return "Type: " + getCategoryName()
                + ", Registration: " + registrationNumber
                + ", Brand: " + brand
                + ", Model: " + model
                + ", Year: " + year
                + ", Seats: " + seats
                + ", Daily Rate: " + DateUtils.formatMoney(dailyRate) + " euro"
                + ", Total Km: " + totalKilometers
                + ", Next Maintenance: " + nextMaintenanceThreshold + " km";
    }

    // getters kai setters mono opou xreiazetai gia na kratame to encapsulation swsto
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public int getSeats() {
        return seats;
    }

    // override tou Bookable contract gia na pairnoun oi managers to daily rate xwris na xeroun ton sygkekrimeno typo
    @Override
    public double getDailyRate() {
        return dailyRate;
    }

    public int getTotalKilometers() {
        return totalKilometers;
    }

    public int getNextMaintenanceThreshold() {
        return nextMaintenanceThreshold;
    }

    public void setNextMaintenanceThreshold(int nextMaintenanceThreshold) {
        this.nextMaintenanceThreshold = nextMaintenanceThreshold;
    }

    // helper validation gia ta koinia stoixeia tou oxhmatos
    // auto deixnei oti kratame tous elegxous konta sto domain model kai oxi mono sto UI
    private void validateCommonData(String registrationNumber, String brand, String model, int year,
            int totalKilometers, int nextMaintenanceThreshold) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("To registration number den prepei na einai keno.");
        }

        if (brand == null || brand.trim().isEmpty() || model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand kai model den prepei na einai kena.");
        }

        if (year < 1900 || year > LocalDate.now().plusYears(1).getYear()) {
            throw new IllegalArgumentException("Mi logiko etos kataskevis.");
        }

        if (totalKilometers < 0) {
            throw new IllegalArgumentException("Ta synolika xiliometra den prepei na einai arnhtika.");
        }

        if (nextMaintenanceThreshold <= totalKilometers) {
            throw new IllegalArgumentException(
                    "To epomeno maintenance threshold prepei na einai megalutero apo ta trexonta km.");
        }
    }
}
