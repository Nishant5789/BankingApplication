package models;

import java.util.ArrayList;
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
            if (amount <= 0) {
                System.out.println("Invalid amount. Credit amount must be greater than zero.");
                return;
            }
            balance += amount;
            transactionHistory.add("Credited: " + amount + " | New Balance: " + balance);
            System.out.println("Amount credited successfully!");
        }

        public void withdraw(double amount) { // Replaced 'debit' with 'withdraw'
            if (amount <= 0) {
                System.out.println("Invalid amount. Withdrawal amount must be greater than zero.");
                return;
            }
            if (amount > balance) {
                System.out.println("Insufficient balance. Withdrawal failed.");
                transactionHistory.add("Failed Withdrawal Attempt: " + amount + " | Available Balance: " + balance);
                return;
            }
            balance -= amount;
            transactionHistory.add("Withdrawn: " + amount + " | New Balance: " + balance);
            System.out.println("Amount withdrawn successfully!");
        }

        public void viewTransactionHistory() {
            System.out.println("Transaction History for " + accountType + " Account:");
            for (String transaction : transactionHistory) {
                System.out.println(transaction);
            }
        }

    }
