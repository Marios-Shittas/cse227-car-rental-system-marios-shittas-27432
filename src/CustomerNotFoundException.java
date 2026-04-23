// custom exception gia na deixnoume oti den vrethike pelatis
// xrhsimopoieitai anti gia generic Exception gia pio katharo minima
public class CustomerNotFoundException extends Exception {

    // o constructor krataei mono to minima giati i klasi xrhsimeuei san pio semantiko exception type
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
