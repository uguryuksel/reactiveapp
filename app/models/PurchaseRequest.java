package models;

public record PurchaseRequest(String userId, String productId, Double amount, String currency) {
}
