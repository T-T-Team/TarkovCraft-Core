package tnt.tarkovcraft.core.client.event;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractClientRegistryEvent<R> extends Event implements IModBusEvent {

    private final R registry;
    private final Function<R, Object> registryLockProvider;

    public AbstractClientRegistryEvent(R registry, Function<R, Object> registryLockProvider) {
        this.registry = registry;
        this.registryLockProvider = registryLockProvider;
    }

    public void register(Consumer<R> handler) {
        Object lock = this.registryLockProvider.apply(this.registry);
        synchronized (lock) {
            handler.accept(this.registry);
        }
    }
}
