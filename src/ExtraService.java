import java.util.ArrayList;
import java.util.List;

// enum gia na exoume statheres epiloges extra services
// to enum einai kalo giati apofeugoume typografika lathi se strings
public enum ExtraService {
    GPS("GPS / Navigation") {
        // override tis abstract calculateCost gia na dwsei xrewsi ana mera mono sto GPS
        @Override
        public double calculateCost(int durationDays) {
            // service xrewsi ana mera
            return durationDays * 5.0;
        }
    },
    CHILD_SEAT("Child Seat") {
        // override tis abstract calculateCost me statheri xrewsi, aneksartita apo tis meres
        @Override
        public double calculateCost(int durationDays) {
            // one-time xrewsi opws zita to assignment
            return 20.0;
        }
    },
    ADDITIONAL_INSURANCE("Additional Insurance") {
        // override tis abstract calculateCost me xrewsi ana blok 5 hmerwn
        @Override
        public double calculateCost(int durationDays) {
            // ceiling logic ana 5-imero period
            return Math.ceil(durationDays / 5.0) * 30.0;
        }
    };

    // friendly onoma gia emfanisi sto menu kai sto invoice
    private String displayName;

    // constructor enum
    ExtraService(String displayName) {
        this.displayName = displayName;
    }

    // abstract method giati kathe service exei diaforetiko tropo xrewsis
    public abstract double calculateCost(int durationDays);

    public String getDisplayName() {
        return displayName;
    }

    // helper methodos gia na fortonoume extras apo flags sto arxeio
    public static ArrayList<ExtraService> fromFlags(boolean gps, boolean childSeat, boolean insurance) {
        ArrayList<ExtraService> services = new ArrayList<>();

        // file persistence: ta true/false flags metatrepontai ksana se enum values
        if (gps) {
            services.add(GPS);
        }
        if (childSeat) {
            services.add(CHILD_SEAT);
        }
        if (insurance) {
            services.add(ADDITIONAL_INSURANCE);
        }

        return services;
    }

    // helper methodos gia pio eukolo elegxo sto save
    public static boolean contains(List<ExtraService> services, ExtraService service) {
        // xrhsimopoieitai san mikro boolean check anti na ksana-grafoume services.contains se polla simeia
        return services.contains(service);
    }
}
