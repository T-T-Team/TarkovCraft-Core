package tnt.tarkovcraft.core.compatibility;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class Component<T> {

    public static final Marker MARKER = MarkerManager.getMarker("Compatibility");

    private final String name;
    private Set<ComponentHolder<T>> holders;
    private final T defaultComponent;
    private final Lazy<T> component;
    private boolean isVanilla = true;

    public Component(String name, T defaultComponent) {
        this.name = name;
        this.holders = new LinkedHashSet<>();
        this.defaultComponent = defaultComponent;
        this.component = Lazy.of(() -> {
            List<ComponentHolder<T>> components = new ArrayList<>(holders);
            components.sort(Comparator.comparingInt(ComponentHolder::order));
            for (ComponentHolder<T> holder : holders) {
                if (holder.loadPredicate.getAsBoolean()) {
                    T value = holder.componentProvider.get();
                    this.holders = null;
                    this.isVanilla = false;
                    TarkovCraftCore.LOGGER.warn(MARKER, "Detected component {} override, will use component {}", name, value);
                    return value;
                }
            }
            this.holders = null;
            TarkovCraftCore.LOGGER.info(MARKER, "No override found for component {} will use default component {}", name, defaultComponent);
            return defaultComponent;
        });
    }

    public boolean isVanilla() {
        return this.isVanilla;
    }

    public synchronized void register(BooleanSupplier predicate, Supplier<T> provider, int order) {
        if (this.holders == null) {
            throw new IllegalStateException("Component registration is not possible at this stage");
        }
        this.holders.add(new ComponentHolder<>(predicate, order, provider));
    }

    public synchronized void register(BooleanSupplier predicate, Supplier<T> provider) {
        this.register(predicate, provider, 0);
    }

    public synchronized void register(String modId, Supplier<T> provider, int order) {
        this.register(() -> ModList.get().isLoaded(modId), provider, order);
    }

    public synchronized void register(String modId, Supplier<T> provider) {
        this.register(modId, provider, 0);
    }

    public T getComponent() {
        return this.component.get();
    }

    public T getDefaultComponent() {
        return defaultComponent;
    }

    public void invalidate() {
        this.component.invalidate();
        TarkovCraftCore.LOGGER.debug(MARKER, "Component '{}' invalidated", name);
    }

    @Override
    public String toString() {
        return "Component{" +
                "name='" + name + '\'' +
                ",component=" + component.get().getClass().getSimpleName() +
                ",vanilla=" + isVanilla +
                '}';
    }

    public record ComponentHolder<T>(BooleanSupplier loadPredicate, int order, Supplier<T> componentProvider) {
    }
}
