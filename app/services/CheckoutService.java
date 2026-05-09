package services;

import actors.InventoryActor;
import messages.ReservationResult;
import messages.ReserveGiftCard;
import models.PurchaseRequest;
import models.Transaction;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.pattern.Patterns;
import repositories.TransactionRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CheckoutService {
    private final TransactionRepository repository;
    private final ActorRef inventoryActor;

    public CheckoutService(TransactionRepository repository, ActorRef inventoryActor) {
        this.repository = repository;
        this.inventoryActor = inventoryActor;
    }

    public CompletableFuture<Transaction> checkout(PurchaseRequest request) {
        Duration timeout = Duration.ofSeconds(2);

        return Patterns.ask(inventoryActor, new ReserveGiftCard(request.productId(), 1), timeout)
                .toCompletableFuture()
                .thenApply(result -> (ReservationResult) result)
                .thenCompose(reservationResult -> {
                    if (!reservationResult.success()) {
                        throw new RuntimeException("Out of stock!");
                    } else {
                        return PaymentService.processCryptoPayment(request.productId(), request.amount())
                                .thenCompose(paymentSuccess -> {
                                    if (paymentSuccess) {
                                        Transaction tr = new Transaction(CheckoutService.generateID().toString(), request.userId(), request.productId(), "Success", Instant.now());
                                        return repository.save(tr);
                                    } else {
                                        throw new RuntimeException("Payment failed!");
                                    }
                                });
                    }
                });


    }

    private static UUID generateID() {
        return UUID.randomUUID();
    }

}
