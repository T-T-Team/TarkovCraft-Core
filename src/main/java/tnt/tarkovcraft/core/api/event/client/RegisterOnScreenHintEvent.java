package tnt.tarkovcraft.core.api.event.client;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import tnt.tarkovcraft.core.client.hint.OnScreenHint;

import java.util.Objects;
import java.util.function.Consumer;

public class RegisterOnScreenHintEvent extends Event implements IModBusEvent {

    private final Consumer<OnScreenHint> consumer;

    public RegisterOnScreenHintEvent(Consumer<OnScreenHint> consumer) {
        this.consumer = consumer;
    }

    public void register(OnScreenHint hint) {
        this.consumer.accept(Objects.requireNonNull(hint, "OnScreenHint cannot be null"));
    }
}
