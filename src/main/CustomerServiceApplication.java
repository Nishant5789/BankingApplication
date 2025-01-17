package main;

import exceptions.AmountIsValidException;
import exceptions.InsufficientBalanceException;
import models.BankAccount;
import models.Customer;
import services.BankAccountService;
import services.CustomerService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CustomerServiceApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CustomerService customerService = new CustomerService();
    private static final BankAccountService bankAccountService = new BankAccountService();

    public static void main(String[] args) {
        System.out.println("Welcome to the Banking System!");
        seedSampleData();

        boolean loggedIn = false;
        Customer loggedInCustomer = null;

        // Login Attempts
        int attempts = 0;
        while (attempts < 3 && !loggedIn) {
            System.out.print("Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            try {
                loggedInCustomer = authenticateUser(username, password);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (loggedInCustomer != null) {
                loggedIn = true;
                System.out.println("Login Successful!");
            } else {
                attempts++;
                System.out.println("Invalid username or password. Attempts left: " + (3 - attempts));
                if (attempts == 3) {
                    System.out.println("Maximum login attempts reached. Forgot Password? (Y/N): ");
                    String choice = scanner.nextLine();
                    if (choice.equalsIgnoreCase("Y")) {
                        forgotPassword();
                        return; // Exit after reset
                    } else {
                        System.out.println("Exiting system. Goodbye!");
                        return;
                    }
                }
            }
        }

        if (loggedIn) {
            showMenu(loggedInCustomer);
        }
    }

    private static Customer authenticateUser(String username, String password) {
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                return customer;
            }
        }
        return null;
    }

    private static void forgotPassword() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        for (Customer customer : customerService.getAllCustomers()) {
            if (customer.getUsername().equals(username)) {
                System.out.println("Answer the security question to reset your password:");
                System.out.println("Question: " + getSecurityQuestion(customer.getSecurityQuestionIndex()));
                System.out.print("Answer: ");
//                System.out.println(customer.getSecurityAnswer());
                String answer = scanner.nextLine();
                if (answer.equals(customer.getSecurityAnswer())) {
                    System.out.print("Enter New Password: ");
                    String newPassword = scanner.nextLine();
                    customer.setPassword(newPassword);
                    System.out.println("Password reset successful! Please log in again.");
                } else {
                    System.out.println("Incorrect answer. Cannot reset password.");
                }
                return;
            }
        }
        System.out.println("Username not found.");
    }

    private static String getSecurityQuestion(int index) {
        String[] questions = {
                "What is your pet's name?",
                "What is your favorite color?",
                "What is your mother's maiden name?"
        };
        return questions[index % questions.length];
    }

    private static void showMenu(Customer customer) {
        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. View Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1 -> viewBalance(customer);
                    case 2 -> depositMoney(customer);
                    case 3 -> withdrawMoney(customer);
                    case 4 -> transferMoney(customer);
                    case 5 -> viewTransactionHistory(customer);
                    case 6 -> {
                        System.out.println("Logged out successfully. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void viewBalance(Customer customer) {
        System.out.println("Your accounts:");
        for (BankAccount account : customer.getAccounts()) {
            System.out.println(account.getAccountType() + " - Balance: " + account.viewBalance());
        }
    }

    private static void depositMoney(Customer customer) {
        Map<Integer, String> mappingAccountType = new HashMap<>();
        mappingAccountType.put(0, "Savings");
        mappingAccountType.put(1, "Salary");
        System.out.print("Choose 0 for  Savings & 1 for Salary): ");
        Integer accountTypeIndex = scanner.nextInt();
        System.out.print("Enter Amount to Deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        // Validate the deposit amount
        if (amount <= 0) {
            throw new AmountIsValidException("Deposit amount must be greater than zero.");
        }


        for (BankAccount account : customer.getAccounts()) {
            if (account.getAccountType().equalsIgnoreCase(mappingAccountType.get(accountTypeIndex))){
                bankAccountService.deposit(account, amount);
                System.out.println("Deposited " + amount + " to " + mappingAccountType.get(accountTypeIndex) + " account. New Balance: " + account.viewBalance());
                return;
            }
        }
        System.out.println("Account type not found.");
    }

    private static void withdrawMoney(Customer customer) {
        Map<Integer, String> mappingAccountType = new HashMap<>();
        mappingAccountType.put(0, "Savings");
        mappingAccountType.put(1, "Salary");
        System.out.print("Choose 0 for  Savings & 1 for Salary): ");
        Integer accountTypeIndex = scanner.nextInt();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        // Validate the deposit amount
        if (amount <= 0) {
            throw new AmountIsValidException("withdraw amount must be greater than zero.");
        }

        for (BankAccount account : customer.getAccounts()) {
            if (account.getAccountType().equalsIgnoreCase(mappingAccountType.get(accountTypeIndex))) {
                try {
                    // Check for sufficient balance
                    if (account.viewBalance() < amount) {
                        throw new InsufficientBalanceException("Insufficient balance in " + mappingAccountType.get(accountTypeIndex) + " account.");
                    }

                    bankAccountService.withdraw(account, amount);
                    System.out.println("Withdrawn " + amount + " from " + mappingAccountType.get(accountTypeIndex) + " account. New Balance: " + account.viewBalance());
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("Account type not found.");
    }

    private static void transferMoney(Customer customer) {
        System.out.print("Enter Recipient Account Number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter Amount to Transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        Customer recipient = customerService.getCustomerByAccountNumber(recipientAccountNumber);
        if (recipient == null) {
            System.out.println("Recipient account not found.");
            return;
        }

        BankAccount senderAccount = customer.getAccounts().get(0); // Assuming single account
        BankAccount recipientAccount = recipient.getAccounts().get(0); // Assuming single account

        try {
            senderAccount.transfer(recipientAccount, amount);
            System.out.println("Transferred " + amount + " to account " + recipientAccountNumber);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewTransactionHistory(Customer customer) {
        for (BankAccount account : customer.getAccounts()) {
            System.out.println("Transaction history for " + account.getAccountType() + " account:");
            account.viewTransactionHistory();
        }
    }

    private static void seedSampleData() {
        // Seed sample customers and accounts
        Customer customer1 = new Customer("Nishant", "C001", "nishant1", "nishant123", 0, "Fluffy",
                "123456", "123 Main St", List.of(new BankAccount("Savings", 1000.0)));
        Customer customer2 = new Customer("Dhruv", "C002", "dhruv1", "dhruv123", 1, "Blue",
                "654321", "456 Elm St", List.of(new BankAccount("Savings", 2000.0)));
        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);
    }
}
