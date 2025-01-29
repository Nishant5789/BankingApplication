package services;

import models.BankAccount;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BankAccountService {
    private CustomerService customerService = new CustomerService();
    private static final String ZMQ_ADDRESS = "tcp://localhost:5555";
    private static final Map<String, String> responseMap = new ConcurrentHashMap<>();
    private static ThreadPoolExecutor executor;

    static {
        executor = new ThreadPoolExecutor(
                2, // Core threads
                4, // Max threads
                60, TimeUnit.SECONDS, // Thread idle timeout
                new ArrayBlockingQueue<>(5), // Queue capacity
                new ThreadPoolExecutor.CallerRunsPolicy() // Custom rejection policy
        );
    }

    public void deposit(BankAccount account, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
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

    public  void transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount) {
        executor.execute(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket dealer = context.createSocket(ZMQ.DEALER);
                dealer.setIdentity(senderAccountNumber.getBytes());
                dealer.connect(ZMQ_ADDRESS);

                String request = senderAccountNumber + "|" + recipientAccountNumber + "|" + amount;
                dealer.send(request);
                responseMap.put(senderAccountNumber, request);

                String response = dealer.recvStr();
                if ("SUCCESS".equals(response)) {
                    BankAccount senderAccount = customerService.getCustomerByAccountNumber(senderAccountNumber).getAccounts().get(0);
                    if (senderAccount != null) {
                        senderAccount.withdraw(amount);
                        responseMap.remove(senderAccount);
                    }
                    System.out.println("Transfer completed successfully.");
                }
                else {
                    System.out.println("Transfer failed: " + response);
                }
            }
        });
    }

    public  void handleTransferMoney() {
        executor.execute(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket responder = context.createSocket(ZMQ.DEALER);
                responder.bind("tcp://*:5556");

                while (!Thread.currentThread().isInterrupted()) {
                    String request = responder.recvStr();
                    if (request == null) break;

                    String[] parts = request.split("\\|");
                    String senderAccount = parts[0];
                    String recipientAccount = parts[1];
                    double amount = Double.parseDouble(parts[2]);

                    BankAccount recipientAccountObj = customerService.getCustomerByAccountNumber(recipientAccount).getAccounts().get(0);
                    if (recipientAccountObj != null) {
                        recipientAccountObj.deposit(amount);
                        responder.send("SUCCESS");
                    } else {
                        responder.send("ERROR: Recipient account not found.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        });
    }
}
