package repositories;

import models.Transaction;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepository {
    private final Map<String, Transaction> memoryDb = new ConcurrentHashMap<>();

    public CompletableFuture<Transaction> save (Transaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                memoryDb.put(transaction.id(), transaction);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return transaction;
        });
    }
}
