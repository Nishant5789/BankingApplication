package services;

import models.Customer;
import models.BankAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerService {
    private static Map<String, Customer>  customerMap = new ConcurrentHashMap<>();; // Map to store customers by account number

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
        // Convert the values of the customerMap to a List and return
        return new ArrayList<>(customerMap.values());
    }

    // Get a customer by account number
    public  Customer getCustomerByAccountNumber(String accountNumber) {
        return customerMap.get(accountNumber);
    }

    // Remove a customer by account number
    public void removeCustomer(String accountNumber) {
        if (customerMap.remove(accountNumber) != null) {
            System.out.println("Customer removed successfully!");
        } else {
            System.out.println("Customer not found.");
        }
    }

    // Display all customers
    public void displayAllCustomers() {
        if (customerMap.isEmpty()) {
            System.out.println("No customers available.");
        } else {
            System.out.println("List of Customers:");
            for (Customer customer : customerMap.values()) {
                System.out.println("Name: " + customer.getName() +
                        ", Account Number: " + customer.getAccountNumber() +
                        ", Address: " + customer.getAddress());
            }
        }
    }

    // Add a bank account to a specific customer
    public void addAccountToCustomer(String accountNumber, BankAccount bankAccount) {
        Customer customer = customerMap.get(accountNumber);
        if (customer != null) {
            customer.addBankAccount(bankAccount);
            System.out.println("Bank account added to customer: " + customer.getName());
        } else {
            System.out.println("Customer not found.");
        }
    }

    // View all accounts of a customer
    public void viewAccountsOfCustomer(String accountNumber) {
        Customer customer = customerMap.get(accountNumber);
        if (customer != null) {
            customer.displayAccounts();
        } else {
            System.out.println("Customer not found.");
        }
    }
}
