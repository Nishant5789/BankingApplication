package models;

import java.util.List;

public class Customer extends Person{
    private String accountNumber;
    private String address;
    private List<BankAccount> accounts;

    public Customer(String name, String id, String username, String password,
                    int securityQuestionIndex, String securityAnswer,
                    String accountNumber, String address, List<BankAccount> accounts) {
        super(name, id, username, password, securityQuestionIndex, securityAnswer);
        this.accountNumber = accountNumber;
        this.address = address;
        this.accounts = accounts;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter and Setter for Accounts
    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    public void addBankAccount(BankAccount account) {
        accounts.add(account);
    }

    public void removeBankAccount(BankAccount account) {
        accounts.remove(account);
    }

    public void displayAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
        } else {
            System.out.println("Accounts for customer: " + name);
            for (BankAccount account : accounts) {
                System.out.println(account);
            }
        }
    }

}
