package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankAccount {
    private String accountType; // (Savings, Current, Salary)
    private double balance;
    private List<String> transactionHistory;


    public BankAccount(String accountType, double initialBalance) {
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        transactionHistory.add("Account created with initial balance: " + initialBalance);
    }

    public String getAccountType() {
        return accountType;
    }

    public double viewBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        System.out.println("Amount Credited successfully!" + " | New Balance: " + balance);
    }

    public void withdraw(double amount) {
        balance -= amount;
        System.out.println("Amount withdrawn successfully!" + " | New Balance: " + balance);
    }

    public void viewTransactionHistory() {
        System.out.println("Transaction History for " + accountType + " Account:");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }


}
