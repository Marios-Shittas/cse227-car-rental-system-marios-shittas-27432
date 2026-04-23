import java.util.ArrayList;

// manager klasi gia pelates
// kratame edw ti logiki gia register kai search wste na min ginetai ola stin Main
public class CustomerManager {

    // lista me tous pelates
    private ArrayList<Customer> customers;

    // DataManager gia save meta apo changes
    private DataManager dataManager;

    // constructor
    public CustomerManager(ArrayList<Customer> customers, DataManager dataManager) {
        this.customers = customers;
        this.dataManager = dataManager;
    }

    // eggrafi neou pelati me auto loyalty account
    public Customer registerCustomer(String fullName, String identityCardNumber, String address, String phoneNumber,
            String email, String creditCardNumber) throws DuplicateCustomerException {
        // exception handling me custom exception gia duplicate customer opws zita to assignment
        if (findByIdentityCardOrNull(identityCardNumber) != null) {
            throw new DuplicateCustomerException("Yparxei idi pelatis me ayto ton arithmo tautothtas.");
        }

        // dimiourgoume kai ta dyo ids prin xtisoume ton Customer wste o "pinakas" customers na menei pliris
        String customerId = IdGenerator.generateCustomerId(customers);
        String loyaltyCard = IdGenerator.generateLoyaltyCardNumber(customers);
        LoyaltyAccount loyaltyAccount = new LoyaltyAccount(loyaltyCard, 0);
        Customer customer = new Customer(customerId, fullName, identityCardNumber, address, phoneNumber, email,
                creditCardNumber, loyaltyAccount);
        customers.add(customer);
        dataManager.saveCustomers(customers);
        return customer;
    }

    // anazitisi me identity card
    public Customer findByIdentityCard(String identityCardNumber) throws CustomerNotFoundException {
        Customer customer = findByIdentityCardOrNull(identityCardNumber);
        if (customer == null) {
            throw new CustomerNotFoundException("Den vrethike pelatis me ayto ton arithmo tautothtas.");
        }
        return customer;
    }

    // anazitisi me loyalty card
    public Customer findByLoyaltyCard(String loyaltyCardNumber) throws CustomerNotFoundException {
        for (Customer customer : customers) {
            if (customer.getLoyaltyAccount().getLoyaltyCardNumber().equalsIgnoreCase(normalizeKey(loyaltyCardNumber))) {
                return customer;
            }
        }
        throw new CustomerNotFoundException("Den vrethike pelatis me ayton ton loyalty card number.");
    }

    // anazitisi me customer id gia reconnect sto loading
    public Customer findByCustomerIdOrNull(String customerId) {
        for (Customer customer : customers) {
            if (customer.getCustomerId().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        return null;
    }

    // helper methodos pou epistrefei null
    public Customer findByIdentityCardOrNull(String identityCardNumber) {
        for (Customer customer : customers) {
            if (customer.getIdentityCardNumber().equalsIgnoreCase(normalizeKey(identityCardNumber))) {
                return customer;
            }
        }
        return null;
    }

    public ArrayList<Customer> getCustomers() {
        // epistrefoume ton in-memory "pinaka" customers gia ektypwseis kai reporting sto UI
        return customers;
    }

    // helper normalization gia na min mas peirazoun mikra/megala grammata i extra kena
    private String normalizeKey(String text) {
        return text == null ? "" : text.trim().toUpperCase();
    }
}
