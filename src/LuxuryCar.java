// klasi gia luxury auta
public class LuxuryCar extends Vehicle {

    // stathero kostos gia luxury
    private static final double DAILY_RATE = 120.0;

    // constructor gia dimiourgia tou antikeimenou
    public LuxuryCar(String registrationNumber, String brand, String model, int year, int seats,
            int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        super(registrationNumber, brand, model, year, seats, DAILY_RATE, totalKilometers, nextMaintenanceThreshold);
    }

    // override tou abstract kanona tis Vehicle gia minimum seats sto luxury category
    @Override
    protected int getMinSeats() {
        return 4;
    }

    // override tou abstract kanona tis Vehicle gia maximum seats sto luxury category
    @Override
    protected int getMaxSeats() {
        return 5;
    }
}
