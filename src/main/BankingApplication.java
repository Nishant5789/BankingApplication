package main;
import models.BankAccount;
import models.Customer;
import services.BankAccountService;
import services.CustomerService;

import java.util.*;

public class BankingApplication {

    private static List<Double>RandomDepositValues = Arrays.asList(
            18202.73, 42536.92, 45692.93, 36259.44, 432.39,
            39691.10, 5767.25, 21430.66, 16900.53, 11099.47,
            204.80, 42445.08, 19076.56, 3663.49, 18511.80,
            27929.25, 49313.36, 26363.09, 49468.58, 28062.72,
            34690.78, 15195.09, 38739.09, 15977.02, 12162.09,
            20290.04, 2201.27, 34975.84, 17298.53, 31987.69,
            13938.88, 18634.94, 47296.32, 16410.90, 7610.94,
            12256.05, 32592.16, 42650.17, 20002.81, 8377.84,
            20427.35, 3055.55, 15963.34, 26087.84, 38007.37,
            5330.23, 14645.27, 13732.82, 12522.80, 129.66
    );
    private static List<Double> RandomWithdrowValues = Arrays.asList(
            3142.81, 2737.14, 8753.15, 3402.43,
            6746.48, 545.69, 2846.35, 955.10,
            9927.16, 8557.67, 9992.72, 2023.17,
            792.09, 8854.21, 3521.46, 7434.98,
            1776.08, 8834.79, 2482.26, 8922.86,
            7669.72, 8404.06, 1257.94, 8738.95,
            8552.41, 8233.10, 2808.80, 7691.81,
            4363.73, 4415.41, 3693.46, 7593.07,
            1997.36, 5628.67, 7067.71, 5050.33,
            264.49, 2516.99, 3231.96, 2302.43,
            2849.13, 5424.28, 5979.55, 7365.56,
            4465.55, 5837.99, 6992.38, 143.22,
            828.50, 8943.38
    );
        // Initialize services
        private static final  CustomerService customerService = new CustomerService();
        private static final  BankAccountService bankAccountService = new BankAccountService();

    public static void main(String[] args) {
        measureThreadExecutionTime();
    }

    public static void measureThreadExecutionTime() {
        // Initialize sample customers
        List<Customer> customers = initializeCustomers(2); // Example for 2 customers
        addCustomersToService(customers, customerService);

        // Measure total execution time for threads
        long startTime = System.currentTimeMillis(); // Start timestamp
        performParallelOperations(customerService, bankAccountService);
        long endTime = System.currentTimeMillis(); // End timestamp

        System.out.println("Total time taken for thread execution: " + (endTime - startTime) + " milliseconds");
        // Fetch and display balances of Savings and Salary accounts
        displaySavingsAndSalaryBalances(customers);
    }

    private static void performParallelOperations(CustomerService customerService, BankAccountService bankAccountService) {
        Random random = new Random();

        // Retrieve all customers
        // List<Customer> customers = customerService.getAllCustomers();
        // Retrieve two custombers
        BankAccount account1 = customerService.getAllCustomers().get(0).getAccounts().get(0);
        BankAccount account2 = customerService.getAllCustomers().get(1).getAccounts().get(0);

        List<Thread> threads = new ArrayList<>(); // List to store all threads

        // For each customer and their accounts, perform deposit and credit in separate threads
        int numThreads = 200;
        for (int k = 0; k < numThreads; k++) {
            int index = k%50;
            // Deposit Task
            double depositAmount = RandomDepositValues.get(index);  // Using k to iterate through the deposit values
            Thread deposit1Thread = new Thread(() -> {
                synchronized (account1){
                    bankAccountService.deposit(account1, depositAmount);
                    System.out.println(Thread.currentThread().getName() + " - Deposited " + depositAmount + " to " + account1.getAccountType());
                }
            });

             // withdraw Task
            double withdrawAmount = RandomWithdrowValues.get(index);  // Using k to iterate through the withdrawal values
            Thread withdraw1Thread = new Thread(() -> {
                synchronized (account1) {
                    try {
                        bankAccountService.withdraw(account1, withdrawAmount);
                        System.out.println(Thread.currentThread().getName() + " - Withdrawn " + withdrawAmount + " from " + account2.getAccountType());
                        ;
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + " - Error during withdrawal: " + e.getMessage());
                    }
                }
            });

            // Add threads to the list
            threads.add(deposit1Thread);
            threads.add(withdraw1Thread);

            // Start the threads
            deposit1Thread.start();
            withdraw1Thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();  // Ensures the main thread waits for all threads to finish
            } catch (InterruptedException e) {
                System.out.println("Error: Thread interrupted - " + e.getMessage());
            }
        }
    }

    // Method to initialize customers with random balances
    public static List<Customer> initializeCustomers(int numberOfCustomers) {
        List<Customer> customers = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= numberOfCustomers; i++) {
            List<BankAccount> accounts = new ArrayList<>();

            // Generate random balances between 0 and 50000
            double savingsBalance = 40000; // random.nextDouble() * 50000;
            double currentBalance = 40000; // random.nextDouble() * 50000;

            accounts.add(new BankAccount("Savings", savingsBalance));
//            accounts.add(new BankAccount("Current", currentBalance));

            Customer customer = new Customer(
                    "Customer" + i,
                    "CUST" + i,
                    "user" + i,
                    "password" + i,
                    0,
                    "Answer" + i,
                    "CUST" + i,
                    "Address " + i,
                    accounts);

            customers.add(customer);
        }
        System.out.println("Sample data initialized with " + numberOfCustomers + " customers having random balances.");
        return customers;
    }

    // Method to add customers to the service
    public static void addCustomersToService(List<Customer> customers, CustomerService customerService) {
        for (Customer customer : customers) {
            customerService.addCustomer(customer);
        }
    }

    // Method to display balances of Savings and Salary accounts
    public static void displaySavingsAndSalaryBalances(List<Customer> customers) {
        System.out.println("\n=== Savings and Salary Account Balances ===");

        for (Customer customer : customers) {
            System.out.println("Customer: " + customer.getName() + " (ID: " + customer.getId() + ")");
            for (BankAccount account : customer.getAccounts()) {
                if (account.getAccountType().equalsIgnoreCase("Savings") || account.getAccountType().equalsIgnoreCase("Salary")) {
                    System.out.println(account.getAccountType() + " Balance: " + account.viewBalance());
                }
            }
        }
    }
}
