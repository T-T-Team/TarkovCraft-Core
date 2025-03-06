package tnt.tarkovcraft.core.client.render;

import tnt.tarkovcraft.core.common.trading.TradeResource;
import tnt.tarkovcraft.core.common.trading.TradeResourceType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class TradeResourceRenderManager {

    private final Map<TradeResourceType<?>, TradeResourceRenderer<?>> rendererMap = new HashMap<>();
    private final Object lock = new Object();

    public <T extends TradeResource> void registerRenderer(TradeResourceType<T> type, TradeResourceRenderer<T> renderer) {
        if (this.rendererMap.put(Objects.requireNonNull(type, "TradeResourceType cannot be null"), Objects.requireNonNull(renderer, "TradeResource renderer cannot be null")) != null) {
            throw new IllegalArgumentException("TradeResource renderer is already registered for type " + type.identifier());
        }
    }

    public <T extends TradeResource> void registerRenderer(Supplier<TradeResourceType<T>> holder, TradeResourceRenderer<T> renderer) {
        this.registerRenderer(holder.get(), renderer);
    }

    @SuppressWarnings("unchecked")
    public <T extends TradeResource> TradeResourceRenderer<T> getRenderer(TradeResourceType<T> type) {
        return (TradeResourceRenderer<T>) this.rendererMap.get(type);
    }

    public Object getLock() {
        return lock;
    }
}
