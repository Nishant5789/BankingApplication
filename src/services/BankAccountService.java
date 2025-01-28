package services;

import models.BankAccount;
import models.Customer;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BankAccountService {
    private static final CustomerService customerService = new CustomerService();
    private static final String ZMQ_ADDRESS = "tcp://localhost:5555";
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

    public  void transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount) {
        executor.execute(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket requester = context.createSocket(ZMQ.REQ);
                ZMQ.Poller poller = context.createPoller(1);
                poller.register(requester, ZMQ.Poller.POLLIN);

                requester.connect(ZMQ_ADDRESS);

                String request = String.format("%s|%s|%f", senderAccountNumber, recipientAccountNumber, amount);
                requester.send(request);

                // Poll for a response with a 5-second timeout
                int events = poller.poll(5000);

                if (events>0 && poller.pollin(0)) {
                    String response = requester.recvStr();
                    if ("SUCCESS".equals(response)) {
                            BankAccount senderAccount = customerService.getCustomerByAccountNumber(senderAccountNumber).getAccounts().get(0);
                        if (senderAccount != null) {
                            senderAccount.withdraw(amount);
                        }
                        System.out.println("Transfer completed successfully.");
                    } else {
                        System.out.println("Transfer failed: " + response);
                    }
                } else {
                    System.out.println("Error: Timeout while waiting for response.");
                }
            } catch (Exception e) {
                System.out.println("Error: Unable to reach the server.");
            }
        });
    }

    public  void handleTransferMoney() {
        executor.execute(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket responder = context.createSocket(ZMQ.REP);
                responder.bind(ZMQ_ADDRESS);

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
