// klasi gia ta economy auta
// edw exoume inheritance giati i klasi klhronomei apo tin abstract Vehicle
public class EconomyCar extends Vehicle {

    // stathero kostos pou orizei to assignment
    private static final double DAILY_RATE = 45.0;

    // constructor pou stelnei ta koinia stoixeia stin vasi klasi
    public EconomyCar(String registrationNumber, String brand, String model, int year, int seats,
            int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        super(registrationNumber, brand, model, year, seats, DAILY_RATE, totalKilometers, nextMaintenanceThreshold);
    }

    // override tou abstract kanona tis Vehicle gia na orisoume to katwtero orio thesewn sto economy
    @Override
    protected int getMinSeats() {
        return 4;
    }

    // override tou abstract kanona tis Vehicle gia na orisoume to anwtero orio thesewn sto economy
    @Override
    protected int getMaxSeats() {
        return 5;
    }
}
