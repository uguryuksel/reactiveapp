package controllers;

import com.google.inject.Inject;
import io.reactivex.rxjava3.core.ObservableSource;
import models.PurchaseRequest;
import models.Transaction;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import repositories.TransactionRepository;
import services.CheckoutService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Flowable;

public class CheckoutController extends Controller {
    private final CheckoutService checkoutService;
    private final TransactionRepository transactionRepository;

    @Inject
    public CheckoutController(CheckoutService checkoutService, TransactionRepository transactionRepository) {
        this.checkoutService = checkoutService;
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<Result> checkout(Http.Request request) {
        PurchaseRequest product = Json.fromJson(request.body().asJson(), PurchaseRequest.class);

        return this.checkoutService.checkout(product).thenApply(transaction -> {
            return ok(Json.toJson(transaction));
        }).exceptionally(exception -> {
            if (exception.getMessage().equals("Out of stock!")) {
                return badRequest(Json.toJson(exception.getMessage()));
            } else if (exception.getMessage().equals("Payment failed!")) {
                return internalServerError(Json.toJson(exception.getMessage()));
            } else {
                return internalServerError(Json.toJson("Unexpected error!"));
            }
        });
    }

    public CompletableFuture<Result> summary() {
        CompletableFuture<List<Transaction>> transactions = transactionRepository.findAll();

        CompletableFuture<Integer> count = transactions.thenApply(list -> list.size());
        CompletableFuture<Double> sum = transactions.thenApply(list -> list.stream().mapToDouble(t -> t.amount()).sum());

        CompletableFuture.allOf(count, sum).join();

        return CompletableFuture.allOf(count, sum).thenApply(v -> {
            Map<String, Object> result = new HashMap<>();
            result.put("count", count.join());
            result.put("sum", sum.join());
            return ok(Json.toJson(result));
        });
    }

    public CompletableFuture<Result> getUserTransactions(String userId) {
        return transactionRepository.findAll().thenApply(
                list -> {
                    List<Transaction> filtered = Observable.fromIterable(list)
                            .filter(t->t.userId().equals(userId))
                            .toList()
                            .blockingGet();
                    return ok(Json.toJson(filtered));
                }
        );
    }
}
