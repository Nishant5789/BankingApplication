package services;

import models.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerService {
    private static Map<String, Customer>  customerMap = new ConcurrentHashMap<>();

    // Add a new customer
    public void addCustomer(Customer customer) {
        if (customerMap.containsKey(customer.getAccountNumber())) {
            System.out.println("Customer with account number already exists.");
        } else {
            customerMap.put(customer.getAccountNumber(), customer);
            System.out.println("Customer added successfully!");
        }
    }

    // get all customber
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    // Get a customer by account number
    public  Customer getCustomerByAccountNumber(String accountNumber) {
        return customerMap.get(accountNumber);
    }
}
