package services;

import java.util.concurrent.CompletableFuture;

public class PaymentService {

    public static CompletableFuture<Boolean> processCryptoPayment (String currency, double amount){
        return CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(5000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        });
    }
}
