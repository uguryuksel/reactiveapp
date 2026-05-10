import actors.InventoryActor;
import com.google.inject.AbstractModule;
import play.libs.pekko.PekkoGuiceSupport;

public class Module extends AbstractModule implements PekkoGuiceSupport {
    @Override
    protected void configure() {
        bindActor(InventoryActor.class, "inventory-actor");
    }
}
