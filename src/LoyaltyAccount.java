// klasi gia to programma pistothtas
// to xorizoume apo ton pelati gia kalytero encapsulation kai pio kathari sxediasi
public class LoyaltyAccount {

    // arithmos kartas loyalty gia anazitisi
    private String loyaltyCardNumber;

    // pontoi pou exei mazepsei o pelatis
    private int points;

    // constructor gia na dimiourgoume loyalty account mazi me ton pelati
    public LoyaltyAccount(String loyaltyCardNumber, int points) {
        if (loyaltyCardNumber == null || loyaltyCardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("To loyalty card number den prepei na einai keno.");
        }

        if (points < 0) {
            throw new IllegalArgumentException("Ta loyalty points den prepei na einai arnhtika.");
        }

        this.loyaltyCardNumber = loyaltyCardNumber.trim().toUpperCase();
        this.points = points;
    }

    // prosthiki pontwn meta apo oloklirwmeni enoikiasi
    // dinei 1 point ana 1 euro pou plirwthike pragmatika
    public int addPointsFromPayment(double amountPaid) {
        // edw h reward logiki meinoun mesa sto LoyaltyAccount gia kalytero encapsulation
        int earnedPoints = (int) Math.floor(amountPaid);
        points += earnedPoints;
        return earnedPoints;
    }

    // elegxos an o pelatis mporei na kanei redeem
    public boolean canRedeem() {
        // kratame ton kanona se mia methodo gia na min skorpizetai to "50+" se olo to project
        return points >= 50;
    }

    // ypologismos megistwn pontwn pou mporoun na xrhsimopoiithoun
    // kratame multiples tou 10 giati 10 points = 1 euro
    public int calculateMaxRedeemablePoints(double totalCost) {
        int maxByPoints = (points / 10) * 10;
        int maxByCost = (int) Math.floor(totalCost) * 10;
        // pairnoume to mikrotero orio gia na min exoume discount megalutero apo auto pou ontws plirwnei o pelatis
        int allowed = Math.min(maxByPoints, maxByCost);

        if (allowed < 50) {
            return 0;
        }

        return allowed;
    }

    // afairei points kai epistrefei to antistoixo discount se euro
    // edw exoume validation gia na min ginontai lathos redeems
    public double redeemPoints(int pointsToRedeem) {
        if (pointsToRedeem == 0) {
            return 0.0;
        }

        if (pointsToRedeem < 50) {
            throw new IllegalArgumentException("To minimum redeem einai 50 points.");
        }

        if (pointsToRedeem % 10 != 0) {
            throw new IllegalArgumentException("Ta points redemption prepei na einai pollaplasio tou 10.");
        }

        if (pointsToRedeem > points) {
            throw new IllegalArgumentException("Den yparxoun arketa loyalty points.");
        }

        points -= pointsToRedeem;
        return pointsToRedeem / 10.0;
    }

    public String getLoyaltyCardNumber() {
        return loyaltyCardNumber;
    }

    public int getPoints() {
        return points;
    }

    // setter mono gia controlled scenarios opws load/restore, kai pali me validation
    public void setPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Ta loyalty points den prepei na einai arnhtika.");
        }
        this.points = points;
    }
}
