import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// klasi gia mia enoikiasi
// sygkentrwnei ton pelati, to oxhma, ta extra, to kostos kai to status se ena object
public class Rental {

    // monadiko id enoikiasis se morfi R001, R002...
    private String rentalId;

    // sindesi me ton pelati
    private Customer customer;

    // sindesi me to epilegmeno oxhma
    private Vehicle vehicle;

    // hmeromhnia enarkshs, inclusive
    private LocalDate startDate;

    // hmeromhnia epistrofis, exclusive gia pio eukolo overlap logic
    private LocalDate endDate;

    // poses xrewsimenes meres krataei i enoikiasi
    private int durationDays;

    // i kathgoria pou zhtise arxika o admin/pelatis
    private String requestedCategory;

    // poses theseis xreiazontai
    private int requiredSeats;

    // lista me ta epipleon services
    private ArrayList<ExtraService> extraServices;

    // analysi kostous gia na mporoume na ektypwsoume invoice
    private double baseCost;
    private double gpsCost;
    private double childSeatCost;
    private double insuranceCost;
    private double discount;
    private double finalPaid;

    // posa loyalty points xrhsimopoihthikan
    private int redeemedPoints;

    // status active/completed gia to bonus kai to history
    private RentalStatus status;

    // constructor gia kainourgia enoikiasi
    // to cost calculation ginetai amesws gia na apothikeuoume olokliro snapshot sto arxeio
    public Rental(String rentalId, Customer customer, Vehicle vehicle, LocalDate startDate, int durationDays,
            String requestedCategory, int requiredSeats, List<ExtraService> extraServices, int redeemedPoints) {
        validateCommonRentalData(rentalId, customer, vehicle, startDate, durationDays, requestedCategory, requiredSeats);

        this.rentalId = rentalId.trim();
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.endDate = startDate.plusDays(durationDays);
        this.requestedCategory = requestedCategory.trim();
        this.requiredSeats = requiredSeats;
        // antigrafoume ti lista gia na kratame to snapshot twn extras tis sygkekrimenis enoikiasis
        this.extraServices = new ArrayList<>(extraServices);
        this.redeemedPoints = redeemedPoints;
        this.status = RentalStatus.ACTIVE;
        calculateCosts();
    }

    // constructor gia fortwma apo arxeio
    // edw kratame ta saved kosth opws htan otan egine i synallagi
    public Rental(String rentalId, Customer customer, Vehicle vehicle, LocalDate startDate, LocalDate endDate,
            int durationDays, String requestedCategory, int requiredSeats, List<ExtraService> extraServices,
            double baseCost, double gpsCost, double childSeatCost, double insuranceCost, double discount,
            double finalPaid, int redeemedPoints, RentalStatus status) {
        validateCommonRentalData(rentalId, customer, vehicle, startDate, durationDays, requestedCategory, requiredSeats);
        DateUtils.validateDateRange(startDate, endDate, "Rental period");

        if (!startDate.plusDays(durationDays).equals(endDate)) {
            throw new IllegalArgumentException("To rental record exei asynistito duration kai end date.");
        }

        this.rentalId = rentalId.trim();
        this.customer = customer;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = durationDays;
        this.requestedCategory = requestedCategory.trim();
        this.requiredSeats = requiredSeats;
        this.extraServices = new ArrayList<>(extraServices);
        this.baseCost = baseCost;
        this.gpsCost = gpsCost;
        this.childSeatCost = childSeatCost;
        this.insuranceCost = insuranceCost;
        this.discount = discount;
        this.finalPaid = finalPaid;
        this.redeemedPoints = redeemedPoints;
        this.status = status;
    }

    // upologismos olou tou kostous
    // kratame ksexwrista ta merh tou invoice gia na einai ksexwristo kai katharo
    public void calculateCosts() {
        // edw to invoice breakdown ypologizetai se xwrista kommatia gia na fainetai kathara stin ergasia
        baseCost = roundMoney(durationDays * vehicle.getDailyRate());
        gpsCost = hasService(ExtraService.GPS) ? roundMoney(ExtraService.GPS.calculateCost(durationDays)) : 0.0;
        childSeatCost = hasService(ExtraService.CHILD_SEAT)
                ? roundMoney(ExtraService.CHILD_SEAT.calculateCost(durationDays))
                : 0.0;
        insuranceCost = hasService(ExtraService.ADDITIONAL_INSURANCE)
                ? roundMoney(ExtraService.ADDITIONAL_INSURANCE.calculateCost(durationDays))
                : 0.0;

        double totalBeforeDiscount = getTotalBeforeDiscount();
        discount = roundMoney(Math.min(redeemedPoints / 10.0, totalBeforeDiscount));
        finalPaid = roundMoney(totalBeforeDiscount - discount);
    }

    // helper gia amount prin to loyalty discount
    public double getTotalBeforeDiscount() {
        return roundMoney(baseCost + gpsCost + childSeatCost + insuranceCost);
    }

    // overlap logic gia availability kai history
    public boolean overlaps(LocalDate requestedStart, LocalDate requestedEnd) {
        // edw xrhsimopoioume to idio overlap rule me olo to project: start inclusive, end exclusive
        return DateUtils.overlaps(startDate, endDate, requestedStart, requestedEnd);
    }

    // helper methodos gia extras
    public boolean hasService(ExtraService service) {
        // mas afinei na diavazoume kathara ton kwdika sta cost calculations kai sto persistence
        return extraServices.contains(service);
    }

    // printable invoice
    public String getInvoiceText() {
        // h invoice methodos menei mesa stin Rental giati to invoice einai snapshot tis synallagis
        StringBuilder builder = new StringBuilder();
        builder.append("\n========== INVOICE ==========\n");
        builder.append("Rental ID: ").append(rentalId).append("\n");
        builder.append("Status: ").append(status).append("\n");
        builder.append("Customer: ").append(customer.getFullName()).append("\n");
        builder.append("Identity Card: ").append(customer.getIdentityCardNumber()).append("\n");
        builder.append("Vehicle: ").append(vehicle.getCategoryName()).append(" - ")
                .append(vehicle.getRegistrationNumber()).append(" - ")
                .append(vehicle.getBrand()).append(" ").append(vehicle.getModel()).append("\n");
        builder.append("Start Date: ").append(startDate).append("\n");
        builder.append("Return Date: ").append(endDate).append("\n");
        builder.append("Duration Days: ").append(durationDays).append("\n");
        builder.append("Base Vehicle Cost: ").append(DateUtils.formatMoney(baseCost)).append(" euro\n");
        builder.append("GPS Cost: ").append(DateUtils.formatMoney(gpsCost)).append(" euro\n");
        builder.append("Child Seat Cost: ").append(DateUtils.formatMoney(childSeatCost)).append(" euro\n");
        builder.append("Additional Insurance Cost: ").append(DateUtils.formatMoney(insuranceCost)).append(" euro\n");
        builder.append("Discount From Loyalty: ").append(DateUtils.formatMoney(discount)).append(" euro\n");
        builder.append("Redeemed Points: ").append(redeemedPoints).append("\n");
        builder.append("Final Paid Amount: ").append(DateUtils.formatMoney(finalPaid)).append(" euro\n");
        builder.append("=============================\n");
        return builder.toString();
    }

    // methodos gia file persistence
    public String toDataString() {
        // edw ginetai file persistence me ids/register numbers anti gia nested serialization
        return rentalId + "|" + customer.getCustomerId() + "|" + vehicle.getRegistrationNumber() + "|" + startDate
                + "|" + endDate + "|" + durationDays + "|" + requestedCategory + "|" + requiredSeats + "|"
                + hasService(ExtraService.GPS) + "|" + hasService(ExtraService.CHILD_SEAT) + "|"
                + hasService(ExtraService.ADDITIONAL_INSURANCE) + "|" + DateUtils.formatMoney(baseCost) + "|"
                + DateUtils.formatMoney(gpsCost) + "|" + DateUtils.formatMoney(childSeatCost) + "|"
                + DateUtils.formatMoney(insuranceCost) + "|" + DateUtils.formatMoney(discount) + "|"
                + DateUtils.formatMoney(finalPaid) + "|" + redeemedPoints + "|" + status;
    }

    // override tou Object.toString gia sintetiki grammh history xwris olo to analitiko invoice
    @Override
    public String toString() {
        return "Rental ID: " + rentalId
                + ", Customer: " + customer.getFullName()
                + ", Vehicle: " + vehicle.getRegistrationNumber()
                + ", Start: " + startDate
                + ", Return: " + endDate
                + ", Duration: " + durationDays
                + ", Final Paid: " + DateUtils.formatMoney(finalPaid) + " euro"
                + ", Status: " + status;
    }

    // mikri methodos gia money rounding gia na min vgazoume poly dekadika
    private double roundMoney(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // helper methodos gia bonus loyalty logic
    // new points dinontai apo to actual amount pou plirwthike meta to discount
    public int calculateEarnedLoyaltyPoints() {
        return (int) Math.floor(finalPaid);
    }

    public String getRentalId() {
        return rentalId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public String getRequestedCategory() {
        return requestedCategory;
    }

    public int getRequiredSeats() {
        return requiredSeats;
    }

    public ArrayList<ExtraService> getExtraServices() {
        // epistrefoume defensive copy gia na min peiraksei kapoios eksw to eswteriko snapshot twn extras
        return new ArrayList<>(extraServices);
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getGpsCost() {
        return gpsCost;
    }

    public double getChildSeatCost() {
        return childSeatCost;
    }

    public double getInsuranceCost() {
        return insuranceCost;
    }

    public double getDiscount() {
        return discount;
    }

    public double getFinalPaid() {
        return finalPaid;
    }

    public int getRedeemedPoints() {
        return redeemedPoints;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    // helper validation gia na kratame to rental model se logiki katastasi
    private void validateCommonRentalData(String rentalId, Customer customer, Vehicle vehicle, LocalDate startDate,
            int durationDays, String requestedCategory, int requiredSeats) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException("To rental ID den prepei na einai keno.");
        }

        if (customer == null || vehicle == null) {
            throw new IllegalArgumentException("Customer kai vehicle den prepei na einai null.");
        }

        if (startDate == null) {
            throw new IllegalArgumentException("To rental start date den prepei na einai null.");
        }

        if (durationDays <= 0) {
            throw new IllegalArgumentException("To rental duration prepei na einai megalutero apo 0.");
        }

        if (requestedCategory == null || requestedCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("H zhtoumeni kathgoria den prepei na einai keni.");
        }

        if (requiredSeats <= 0) {
            throw new IllegalArgumentException("Oi zhtoumenes theseis prepei na einai thetikes.");
        }
    }
}
