import java.time.LocalDate;
import java.util.List;

// interface gia na deixoume oti ola ta oxhmata mporoun na elegxthoun gia diathesimothta
// edw exoume interface usage giati theloume ena koino "symvolaio" xwris na desmeuoume tin ylopoihsh
public interface Bookable {

    // methodos pou mas leei an ena oxhma einai eleuthero se ena xroniko diasthma
    // auto to contract to ylopoiei i Vehicle kai oi ypoklaseis tis to klhronomoun
    // ta rentals kai maintenanceRecords leitourgoun san oi dyo "pinakes" tis trexousas katastasis stin mnimi
    boolean isAvailable(LocalDate startDate, LocalDate endDate, List<Rental> rentals,
            List<MaintenanceRecord> maintenanceRecords);

    // methodos gia to hmerhsio kostos, wste na douleuoume me Bookable/Vehicle references
    // edw fainetai kai polymorphism giati mporoume na exoume koini antimetwpisi gia diaforetika vehicle objects
    double getDailyRate();
}
