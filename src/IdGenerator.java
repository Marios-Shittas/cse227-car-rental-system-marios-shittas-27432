import java.util.List;

// utility klasi gia auto-generated ids
// krataei tin logiki se ena simeio gia na min antigrafoume ton idio kwdika
public class IdGenerator {

    // private constructor giati den xreiazomaste instances
    private IdGenerator() {
    }

    // dimiourgia epomenou rental id se morfi R001, R002, ...
    public static String generateRentalId(List<Rental> rentals) {
        int max = 0;
        // skanaroume olo ton "pinaka" rentals kai pairnoume ton megalitero arithmo pou yparxei idi
        for (Rental rental : rentals) {
            max = Math.max(max, extractNumber(rental.getRentalId(), "R"));
        }
        return formatId("R", max + 1);
    }

    // dimiourgia epomenou customer id
    public static String generateCustomerId(List<Customer> customers) {
        int max = 0;
        for (Customer customer : customers) {
            max = Math.max(max, extractNumber(customer.getCustomerId(), "C"));
        }
        return formatId("C", max + 1);
    }

    // dimiourgia loyalty card number
    public static String generateLoyaltyCardNumber(List<Customer> customers) {
        int max = 0;
        for (Customer customer : customers) {
            max = Math.max(max,
                    extractNumber(customer.getLoyaltyAccount().getLoyaltyCardNumber(), "L"));
        }
        return formatId("L", max + 1);
    }

    // dimiourgia maintenance id
    public static String generateMaintenanceId(List<MaintenanceRecord> records) {
        int max = 0;
        for (MaintenanceRecord record : records) {
            max = Math.max(max, extractNumber(record.getMaintenanceId(), "M"));
        }
        return formatId("M", max + 1);
    }

    // helper methodos pou pairnei ton arithmo meta to prefix
    private static int extractNumber(String id, String prefix) {
        try {
            return Integer.parseInt(id.replace(prefix, ""));
        } catch (NumberFormatException exception) {
            // an kapoio palio id den einai swsta formatarismeno, den spaei olo to generation kai aplws agnoeitai
            return 0;
        }
    }

    // helper methodos gia statheri morfi me leading zeroes
    private static String formatId(String prefix, int value) {
        // me to %03d exoume stathero mikos id gia pio katharo sorting kai emfanisi
        return String.format("%s%03d", prefix, value);
    }
}
