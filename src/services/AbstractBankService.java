package services;

import models.BankAccount;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBankService {
    protected static final String ZMQ_ADDRESS = "tcp://localhost:5555";
    protected static final Map<String, String> responseMap = new ConcurrentHashMap<>();
    protected static final ThreadPoolExecutor executor;

    static {
        executor = new ThreadPoolExecutor(
                2, // Core threads
                4, // Max threads
                60, TimeUnit.SECONDS, // Thread idle timeout
                new ArrayBlockingQueue<>(5), // Queue capacity
                new ThreadPoolExecutor.CallerRunsPolicy() // Custom rejection policy
        );
    }

    public abstract void deposit(BankAccount account, double amount);
    public abstract void withdraw(BankAccount account, double amount);
    public abstract void viewTransactionHistory(BankAccount account);
    public abstract void transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount);
    public abstract void handleTransferMoney();
}
