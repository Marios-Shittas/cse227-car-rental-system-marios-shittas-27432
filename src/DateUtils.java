import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

// utility klasi gia dates kai money formatting
// to xorizoume apo tin Main gia na min mazeyontai ola se ena simeio
public class DateUtils {

    // private constructor giati einai utility class kai den theloume objects
    private DateUtils() {
    }

    // helper validation gia na exoume panta swsto start/end range
    // auto bohthaei na einai synexes to overlap logic se olo to project
    public static void validateDateRange(LocalDate startDate, LocalDate endDate, String context) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(context + ": Oi hmeromhnies den prepei na einai null.");
        }

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                    context + ": To end date prepei na einai meta apo to start date.");
        }
    }

    // helper gia overlap me start inclusive kai end exclusive
    // auto to design aplopoiei poly ta date checks
    public static boolean overlaps(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        // edw kratame mia koini methodo gia overlap logic gia na min exoume diaforetika rules se kathe klasi
        validateDateRange(start1, end1, "Range 1");
        validateDateRange(start2, end2, "Range 2");
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // parse date me exception handling gia invalid user input
    public static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Mi egkyri hmeromhnia. Xrhsimopoihse morfi yyyy-MM-dd.");
        }
    }

    // format money me 2 dekadika gia na fainetai pio swsto sto invoice
    public static String formatMoney(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    // elegxos an mia mera einai working day
    public static boolean isWorkingDay(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    // vriskoume tin epomeni i idia working day gia na ksekinaei sosta to maintenance
    public static LocalDate nextOrSameWorkingDay(LocalDate date) {
        LocalDate result = date;
        // an pesei savvatokyriako, proxwrame mera-mera mexri working day
        while (!isWorkingDay(result)) {
            result = result.plusDays(1);
        }
        return result;
    }

    // ypologismos exclusve telous periodou me working days mono
    // paradeigma: start Monday kai 2 meres -> end Wednesday
    public static LocalDate calculateWorkingEndExclusive(LocalDate startDate, int workingDays) {
        if (workingDays <= 0) {
            throw new IllegalArgumentException("Ta working days prepei na einai megalutera apo 0.");
        }

        LocalDate cursor = startDate;
        int countedDays = 0;

        // o cursor proxwra mia mera ti fora, alla metrame mono osones einai working days
        while (countedDays < workingDays) {
            if (isWorkingDay(cursor)) {
                countedDays++;
            }
            cursor = cursor.plusDays(1);
        }

        return cursor;
    }

    // eidiki methodos gia maintenance
    // to vehicle ginetai unavailable amesws, akoma ki an h prwti working day einai meta apo savvatokyriako
    public static LocalDate calculateMaintenanceEndExclusive(LocalDate unavailableFrom, int workingDays) {
        // to pragmatiko service start mporei na metatopistei, alla to block availability ksekina apo unavailableFrom
        LocalDate serviceStart = nextOrSameWorkingDay(unavailableFrom);
        return calculateWorkingEndExclusive(serviceStart, workingDays);
    }
}
