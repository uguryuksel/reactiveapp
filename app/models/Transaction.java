package models;

import java.time.Instant;

public record Transaction(String id, String userId, String productId, String status, Instant timestamp) {
}
