package models;

import java.util.List;

public class Customer extends Person{
    private String accountNumber;
    private String address;
    protected String username;
    private String password; // Storing password as String
    private String[] securityQuestions = {
            "What's your first watch movie?",
            "Who's your favorite sportsperson?",
            "What's your hobby?"
    };
    private int securityQuestionIndex; // Stores the chosen question index
    private String securityAnswer; // Stores the answer (lowercased for case-insensitivity)
    private List<BankAccount> accounts;

    public Customer(String name, String id, String username, String password,
                    int securityQuestionIndex, String securityAnswer,
                    String accountNumber, String address, List<BankAccount> accounts) {
        super(name, id, username, password, securityQuestionIndex, securityAnswer);
        this.accountNumber = accountNumber;
        this.address = address;
        this.username = username;
        this.password = password; // Storing password as String
        this.securityQuestionIndex = securityQuestionIndex;
        this.securityAnswer = securityAnswer.toLowerCase().trim(); // Normalize the answer for comparison
        this.accounts = accounts;
    }

    public String getName() {
        return this.name;
    }

    public Object getUsername() {
        return this.username;
    }

    public int getSecurityQuestionIndex() {
        return this.securityQuestionIndex;
    }

    public String getSecurityAnswer() {
        return this.securityAnswer;
    }

    public Object getPassword() {
        return this.password;
    }
    public void setPassword(String newPassword) {
        this.password=newPassword;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public String getAccountNumber() {
        return  this.accountNumber;
    }

    public void addBankAccount(BankAccount bankAccount) {
        this.accounts.add(bankAccount);
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
