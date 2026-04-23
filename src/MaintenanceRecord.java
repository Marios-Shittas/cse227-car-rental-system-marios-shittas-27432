import java.time.LocalDate;

// klasi gia to bonus meros tou maintenance history
// to xorizoume se diko tou object gia na kratame katharo istoriko synthrhsewn
public class MaintenanceRecord {

    // monadiko id maintenance
    private String maintenanceId;

    // to oxhma sto opoio antistoixei i synthrhsi
    private Vehicle vehicle;

    // arxi periodou maintenance, inclusive
    private LocalDate startDate;

    // telos periodou maintenance, exclusive gia pio eukolo overlap checking
    private LocalDate endDate;

    // posa km eixe to oxhma otan ekane service
    private int kilometersAtService;

    // mikri perigrafi ergasiwn
    private String description;

    // flag gia na deixnoume an exei oloklirwthei to maintenance
    private boolean completed;

    // constructor gia dimiourgia maintenance eggrafis
    public MaintenanceRecord(String maintenanceId, Vehicle vehicle, LocalDate startDate, LocalDate endDate,
            int kilometersAtService, String description, boolean completed) {
        // edw kratame to maintenance ws xwristo history object gia na min fortwnetai h Vehicle me panta
        if (maintenanceId == null || maintenanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("To maintenance ID den prepei na einai keno.");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("To vehicle sto maintenance den prepei na einai null.");
        }
        DateUtils.validateDateRange(startDate, endDate, "Maintenance period");
        if (kilometersAtService < 0) {
            throw new IllegalArgumentException("Ta kilometers service den prepei na einai arnhtika.");
        }

        this.maintenanceId = maintenanceId.trim().toUpperCase();
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.kilometersAtService = kilometersAtService;
        this.description = description == null ? "" : description.trim();
        this.completed = completed;
    }

    // overlap check gia na mporoume na blokaroume to oxhma se booking/availability
    public boolean overlaps(LocalDate requestedStart, LocalDate requestedEnd) {
        // xrhsimopoioume to idio overlap convention me rentals gia na menei synexes to availability model
        return DateUtils.overlaps(startDate, endDate, requestedStart, requestedEnd);
    }

    // methodos gia file persistence
    public String toDataString() {
        // file persistence me registration number anti gia nested vehicle data
        return maintenanceId + "|" + vehicle.getRegistrationNumber() + "|" + startDate + "|" + endDate + "|"
                + kilometersAtService + "|" + description + "|" + completed;
    }

    // override tou Object.toString gia na fainetai eukola to maintenance history sta menus kai sto debug
    @Override
    public String toString() {
        return "Maintenance ID: " + maintenanceId
                + ", Vehicle: " + vehicle.getRegistrationNumber()
                + ", Start: " + startDate
                + ", End Exclusive: " + endDate
                + ", Available Again: " + endDate
                + ", Km At Service: " + kilometersAtService
                + ", Description: " + description
                + ", Completed: " + completed;
    }

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getKilometersAtService() {
        return kilometersAtService;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
