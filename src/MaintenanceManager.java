import java.time.LocalDate;
import java.util.ArrayList;

// manager klasi gia to bonus maintenance subsystem
// kratame se ena simeio scheduling, history kai status refresh
public class MaintenanceManager {

    // lista me ola ta maintenance records
    private ArrayList<MaintenanceRecord> maintenanceRecords;

    // DataManager gia persistence
    private DataManager dataManager;

    // constructor
    public MaintenanceManager(ArrayList<MaintenanceRecord> maintenanceRecords, DataManager dataManager) {
        // to maintenanceRecords leitourgei san o in-memory "pinakas" olou tou maintenance subsystem
        this.maintenanceRecords = maintenanceRecords;
        this.dataManager = dataManager;
    }

    // automatic scheduling maintenance gia 2 working days
    // edw to bonus meros ginetai real feature kai oxi aplh ektypwsi
    public MaintenanceRecord scheduleMaintenance(Vehicle vehicle, LocalDate availableFrom, String description) {
        // bonus rule: apo ti stigmi pou to oxhma xreiazetai service, ginetai unavailable amesws
        // akoma ki an peftei savvatokyriako, menei blocked mexri na teleiwsei to 2-working-days maintenance
        LocalDate startDate = availableFrom;
        LocalDate endDate = DateUtils.calculateMaintenanceEndExclusive(startDate, 2);
        boolean completed = !endDate.isAfter(LocalDate.now());

        MaintenanceRecord record = new MaintenanceRecord(
                IdGenerator.generateMaintenanceId(maintenanceRecords),
                vehicle,
                startDate,
                endDate,
                vehicle.getTotalKilometers(),
                description,
                completed);

        // molis dimiourgithei to record, mpainei amesws sto istoriko kai meta ginetai save
        maintenanceRecords.add(record);
        dataManager.saveMaintenanceRecords(maintenanceRecords);
        return record;
    }

    // refresh completion flags me vasi tin trexousa hmeromhnia
    public void refreshCompletionStatuses(LocalDate referenceDate) {
        boolean changed = false;

        for (MaintenanceRecord record : maintenanceRecords) {
            if (!record.isCompleted() && !record.getEndDate().isAfter(referenceDate)) {
                // edw ginetai update se stored state kai meta save sto arxeio
                record.setCompleted(true);
                changed = true;
            }
        }

        if (changed) {
            dataManager.saveMaintenanceRecords(maintenanceRecords);
        }
    }

    // istoriko maintenance ana oxhma
    public ArrayList<MaintenanceRecord> getRecordsForVehicle(String registrationNumber) {
        ArrayList<MaintenanceRecord> results = new ArrayList<>();

        for (MaintenanceRecord record : maintenanceRecords) {
            if (record.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)) {
                results.add(record);
            }
        }

        return results;
    }

    // records pou mplokaroun ena sygkekrimeno date range
    public ArrayList<MaintenanceRecord> getBlockingRecordsForVehicle(String registrationNumber, LocalDate startDate,
            LocalDate endDate) {
        ArrayList<MaintenanceRecord> results = new ArrayList<>();

        for (MaintenanceRecord record : maintenanceRecords) {
            if (record.getVehicle().getRegistrationNumber().equalsIgnoreCase(registrationNumber)
                    && record.overlaps(startDate, endDate)) {
                results.add(record);
            }
        }

        return results;
    }

    public ArrayList<MaintenanceRecord> getMaintenanceRecords() {
        // epistrefei ton pliri "pinaka" maintenance gia availability checks kai ektypwseis
        return maintenanceRecords;
    }
}
