package controllers;

import com.google.inject.Inject;
import models.PurchaseRequest;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.CheckoutService;

import java.util.concurrent.CompletableFuture;

public class CheckoutController extends Controller {
    private final CheckoutService checkoutService;

    @Inject
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    public CompletableFuture<Result> checkout(Http.Request request) {
        PurchaseRequest product = Json.fromJson(request.body().asJson(), PurchaseRequest.class);

        return this.checkoutService.checkout(product).thenApply(transaction -> {
            return ok(Json.toJson(transaction));
        });
    }
}
