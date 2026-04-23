// klasi pou anaparista enan pelati tis etairias
// edw exoume encapsulation giati ta prosopika stoixeia einai private
public class Customer {

    // eswteriko id gia na ginetai syndesi me tis enoikiaseis sto arxeio
    private String customerId;

    // plires onoma pelati
    private String fullName;

    // arithmos tautothtas, prepei na einai monadikos
    private String identityCardNumber;

    // dieuthynsi
    private String address;

    // tilefwno epikoinwnias
    private String phoneNumber;

    // email epikoinwnias
    private String email;

    // arithmos pistwtikhs kartas gia tin ergasia
    private String creditCardNumber;

    // syndesh me loyalty account
    private LoyaltyAccount loyaltyAccount;

    // constructor pou mazeuei ola ta stoixeia se ena object
    public Customer(String customerId, String fullName, String identityCardNumber, String address, String phoneNumber,
            String email, String creditCardNumber, LoyaltyAccount loyaltyAccount) {
        // edw exoume encapsulation kai controlled initialization gia na min ftiaxnoume "misa" customer objects
        validateCustomerData(customerId, fullName, identityCardNumber, address, phoneNumber, email, creditCardNumber,
                loyaltyAccount);

        this.customerId = customerId.trim().toUpperCase();
        this.fullName = fullName.trim();
        this.identityCardNumber = identityCardNumber.trim().toUpperCase();
        this.address = address.trim();
        this.phoneNumber = phoneNumber.trim();
        this.email = email.trim();
        this.creditCardNumber = creditCardNumber.trim();
        this.loyaltyAccount = loyaltyAccount;
    }

    // methodos gia file persistence
    public String toDataString() {
        // file persistence: kratame mono ta stoixeia pou xreiazontai gia na xanaktistei to object sto startup
        return customerId + "|" + fullName + "|" + identityCardNumber + "|" + address + "|" + phoneNumber + "|"
                + email + "|" + creditCardNumber + "|" + loyaltyAccount.getLoyaltyCardNumber() + "|"
                + loyaltyAccount.getPoints();
    }

    // short summary gia lists kai menus
    public String getShortSummary() {
        return customerId + " - " + fullName + " (ID Card: " + identityCardNumber
                + ", Loyalty: " + loyaltyAccount.getLoyaltyCardNumber() + ", Points: "
                + loyaltyAccount.getPoints() + ")";
    }

    // override tou Object.toString gia na ektypwnoume ton pelati se readable morfi kai oxi se memory reference
    @Override
    public String toString() {
        return "Customer ID: " + customerId
                + "\nFull Name: " + fullName
                + "\nIdentity Card: " + identityCardNumber
                + "\nAddress: " + address
                + "\nPhone: " + phoneNumber
                + "\nEmail: " + email
                + "\nCredit Card: " + creditCardNumber
                + "\nLoyalty Card: " + loyaltyAccount.getLoyaltyCardNumber()
                + "\nLoyalty Points: " + loyaltyAccount.getPoints();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public LoyaltyAccount getLoyaltyAccount() {
        return loyaltyAccount;
    }

    // helper validation gia pio katharo constructor
    private void validateCustomerData(String customerId, String fullName, String identityCardNumber, String address,
            String phoneNumber, String email, String creditCardNumber, LoyaltyAccount loyaltyAccount) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("To customer ID den prepei na einai keno.");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("To full name den prepei na einai keno.");
        }
        if (identityCardNumber == null || identityCardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("O arithmos tautothtas den prepei na einai kenos.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("H dieuthynsi den prepei na einai keni.");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("To tilefwno den prepei na einai keno.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("To email den prepei na einai keno.");
        }
        if (creditCardNumber == null || creditCardNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("H credit card den prepei na einai keni.");
        }
        if (loyaltyAccount == null) {
            throw new IllegalArgumentException("To loyalty account den prepei na einai null.");
        }
    }
}
