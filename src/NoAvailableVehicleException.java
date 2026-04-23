// custom exception gia periptwseis pou den yparxei diathesimo oxhma
public class NoAvailableVehicleException extends Exception {

    // mas afinei na xorisoume to "den vrethike diathesimo" apo ena geniko validation error
    public NoAvailableVehicleException(String message) {
        super(message);
    }
}
