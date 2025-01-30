package services;

import models.BankAccount;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Date;

public class BankAccountService extends AbstractBankService {
    private CustomerService customerService = new CustomerService();

    @Override
    public void deposit(BankAccount account, double amount) {
        account.deposit(amount);
        account.getTransactionHistory().add(amount + " is deposit at" + new Date());
    }

    @Override
    public void withdraw(BankAccount account, double amount) {
        account.withdraw(amount);
        account.getTransactionHistory().add(amount + " is withdraw at" + new Date());
    }

    @Override
    public void viewTransactionHistory(BankAccount account) {
        account.viewTransactionHistory();
    }

    @Override
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
                        senderAccount.getTransactionHistory().add(amount+" transfer to acc - " + recipientAccountNumber);
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

    @Override
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
                        recipientAccountObj.getTransactionHistory().add(amount+" transfer from acc - " + senderAccount);
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
