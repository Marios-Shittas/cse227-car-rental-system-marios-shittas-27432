// klasi gia SUV oxhmata me diko tous rate kai seat policy
public class SUV extends Vehicle {

    // stathero kostos gia SUV
    private static final double DAILY_RATE = 70.0;

    // constructor pou xrhsimopoiei ton super constructor
    public SUV(String registrationNumber, String brand, String model, int year, int seats,
            int totalKilometers, int nextMaintenanceThreshold) throws InvalidSeatCountException {
        super(registrationNumber, brand, model, year, seats, DAILY_RATE, totalKilometers, nextMaintenanceThreshold);
    }

    // override tou abstract kanona tis Vehicle gia na deixoume oti ta SUV ksekinoun apo 5 theseis
    @Override
    protected int getMinSeats() {
        return 5;
    }

    // override tou abstract kanona tis Vehicle gia na kleisei to epitrepto range mexri 7 theseis
    @Override
    protected int getMaxSeats() {
        return 7;
    }
}
