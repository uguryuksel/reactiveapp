package actors;

import messages.ReservationResult;
import messages.ReserveGiftCard;
import org.apache.pekko.actor.AbstractActor;

import java.util.HashMap;
import java.util.Map;

public class InventoryActor extends AbstractActor {
    private final Map<String, Integer> inventory = new HashMap<>();

    public InventoryActor() {
        inventory.put("STEAM_10", 10);
        inventory.put("Epic_20", 10);
        inventory.put("Unity_10", 10);
        inventory.put("G2A_10", 10);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ReserveGiftCard.class, this::handleReservation).build();
    }

    private void handleReservation(ReserveGiftCard reserveGiftCard) {
        int quantity = inventory.getOrDefault(reserveGiftCard.productId(), 0);

        if(quantity >= reserveGiftCard.quantity()) {
            inventory.put(reserveGiftCard.productId(), quantity - reserveGiftCard.quantity());
            ReservationResult result = new ReservationResult(true, "Gift card reserved successfully");
            getSender().tell(result, getSelf());
        }
        else {
            ReservationResult result = new ReservationResult(false, "Gift card reservation failed");
            getSender().tell(result, getSelf());
        }
    }
}
