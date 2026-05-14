package services;

import actors.InventoryActor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
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
import java.util.concurrent.TimeUnit;

public class CheckoutService {
    private final TransactionRepository repository;
    private final ActorRef inventoryActor;

    @Inject
    public CheckoutService(TransactionRepository repository, @Named("inventory-actor")ActorRef inventoryActor) {
        this.repository = repository;
        this.inventoryActor = inventoryActor;
    }

    public CompletableFuture<Transaction> checkout(PurchaseRequest request) {
        Duration timeout = Duration.ofSeconds(2);

        return Patterns.ask(inventoryActor, new ReserveGiftCard(request.productId(), request.quantity()), timeout)
                .toCompletableFuture()
                .thenApply(result -> (ReservationResult) result)
                .thenCompose(reservationResult -> {
                    if (!reservationResult.success()) {
                        throw new RuntimeException("Out of stock!");
                    } else {
                        return PaymentService.processCryptoPayment(request.productId(), request.amount())
                                .thenCompose(paymentSuccess -> {
                                    if (paymentSuccess) {
                                        Transaction tr = new Transaction(CheckoutService.generateID().toString(), request.userId(), request.productId(), "Success", Instant.now(), request.amount());
                                        return repository.save(tr);
                                    } else {
                                        throw new RuntimeException("Payment failed!");
                                    }
                                }).orTimeout(3, TimeUnit.SECONDS);
                    }
                });


    }

    private static UUID generateID() {
        return UUID.randomUUID();
    }

}
