import java.time.LocalDate;
import java.util.Scanner;

// helper klasi gia console input
// to xorizoume giati to validation tou user input einai megalo meros tou assignment
public class InputHelper {

    // ena Scanner gia olo to programma
    private Scanner scanner;

    // constructor
    public InputHelper() {
        scanner = new Scanner(System.in);
    }

    // diavasma mh kenou string
    public String readNonEmptyString(String prompt) {
        // to loop stamata mono otan paroume timi pou den einai adeia meta to trim
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("To pedio den mporei na einai keno.");
        }
    }

    // diavasma integer me exception handling
    public int readInt(String prompt) {
        // kratame tin prospatheia edw oste i Main na pairnei panta katharo int kai oxi raw string
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException exception) {
                System.out.println("Dwse egkyro akeraio arithmo.");
            }
        }
    }

    // diavasma thetikou integer
    public int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Dwse thetiko arithmo megalutero tou 0.");
        }
    }

    // diavasma int se sygkekrimeno range
    public int readMenuChoice(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Dwse epilogi apo " + min + " mexri " + max + ".");
        }
    }

    // diavasma LocalDate me morfi yyyy-MM-dd
    public LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return DateUtils.parseDate(scanner.nextLine().trim());
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    // helper gia na valoume kanona oti mia hmeromhnia den einai prin apo ena minimum
    public LocalDate readDateNotBefore(String prompt, LocalDate minimumDate) {
        while (true) {
            LocalDate value = readDate(prompt);
            if (!value.isBefore(minimumDate)) {
                return value;
            }
            System.out.println("Dwse hmeromhnia apo " + minimumDate + " kai meta.");
        }
    }

    // diavasma yes/no
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toLowerCase();

            // to startsWith("y") mas dinei ena mikro, aplo mapping gia yes/y
            if (value.equals("y") || value.equals("yes") || value.equals("n") || value.equals("no")) {
                return value.startsWith("y");
            }

            System.out.println("Dwse yes/y i no/n.");
        }
    }

    // kleisimo scanner sto telos
    public void close() {
        scanner.close();
    }
}
