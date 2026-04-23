// custom exception gia duplicate identity card
public class DuplicateCustomerException extends Exception {

    // o constructor mas dinei custom minima analoga me to poio stoixeio prokalese to duplicate
    public DuplicateCustomerException(String message) {
        super(message);
    }
}
