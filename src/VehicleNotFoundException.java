// custom exception gia periptwseis pou psaxnoume oxhma kai den to vriskoume
public class VehicleNotFoundException extends Exception {

    // xrhsimeuei kyriws se search/delete roes gia na exoume pio katharo minima sto console UI
    public VehicleNotFoundException(String message) {
        super(message);
    }
}
