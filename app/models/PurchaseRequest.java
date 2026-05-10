package models;

public record PurchaseRequest(String userId, String productId, int quantity, Double amount, String currency) {
}
