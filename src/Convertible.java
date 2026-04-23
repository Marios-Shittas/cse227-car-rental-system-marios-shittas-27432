// klasi gia convertible auta
public class Convertible extends Vehicle {

    // stathero kostos gia convertible
    private static final double DAILY_RATE = 90.0;

    // constructor gia dimiourgia valid convertible object
    public Convertible(String registrationNumber, String brand, String model, int year, int seats,
            int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        super(registrationNumber, brand, model, year, seats, DAILY_RATE, totalKilometers, nextMaintenanceThreshold);
    }

    // override tou abstract kanona tis Vehicle giati ta convertible dexontai kai 2theseis lyseis
    @Override
    protected int getMinSeats() {
        return 2;
    }

    // override tou abstract kanona tis Vehicle gia na min pernaei ta 4 kathismata se auth tin kathgoria
    @Override
    protected int getMaxSeats() {
        return 4;
    }
}
