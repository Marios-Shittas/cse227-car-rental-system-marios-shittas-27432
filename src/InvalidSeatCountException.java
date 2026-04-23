// custom exception gia invalid arithmo thesewn ana kathgoria
public class InvalidSeatCountException extends Exception {

    // xrhsimopoieitai ston constructor ton vehicles otan ta overridden seat rules den ikanopoiountai
    public InvalidSeatCountException(String message) {
        super(message);
    }
}
