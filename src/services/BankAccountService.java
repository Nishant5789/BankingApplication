package services;

import models.BankAccount;

public class BankAccountService {

    public void deposit(BankAccount account, double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be greater than zero.");
            return;
        }
        account.deposit(amount);
    }

    public void withdraw(BankAccount account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }
        if (amount > account.viewBalance()) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal.");
        }
        account.withdraw(amount);
    }

    public double getBalance(BankAccount account) {
        return account.viewBalance();
    }

    public void viewTransactionHistory(BankAccount account) {
        account.viewTransactionHistory();
    }
}
