package tnt.tarkovcraft.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class EventHandler<T> {

    private final List<T> listeners;

    private EventHandler() {
        this.listeners = new ArrayList<>();
    }

    public static <T> EventHandler<T> create() {
        return new EventHandler<>();
    }

    public void subscribe(T listener) {
        this.listeners.add(listener);
    }

    public void unsubscribe(T listener) {
        this.listeners.remove(listener);
    }

    public void dispatch(Consumer<T> dispatcher) {
        this.listeners.forEach(dispatcher);
    }

    public int subscriberCount() {
        return this.listeners.size();
    }
}
