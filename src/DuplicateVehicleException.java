// custom exception gia duplicate registration number
public class DuplicateVehicleException extends Exception {

    // etsi to UI pairnei akribes minima gia registration conflicts xwris na xanei ton typo tis eksairesis
    public DuplicateVehicleException(String message) {
        super(message);
    }
}
