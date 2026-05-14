package repositories;

import models.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public CompletableFuture<List<Transaction>> findAll () {
        return CompletableFuture.supplyAsync(()-> new ArrayList<>(memoryDb.values()));
    }
}
