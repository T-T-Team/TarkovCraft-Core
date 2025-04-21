package tnt.tarkovcraft.core.util.context;

import net.minecraft.util.context.ContextKey;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface Context {

    Context NONE = ContextImpl.empty();

    Set<ContextKey<?>> listKeys();

    <T> Optional<T> get(ContextKey<T> key);

    boolean containsKey(ContextKey<?> key);

    boolean containsKeys(Collection<ContextKey<?>> keys);

    default <T> T getOrDefault(ContextKey<T> k, T defaultValue) {
        return this.get(k).orElse(defaultValue);
    }

    default <T> T getOrSupplyDefault(ContextKey<T> k, Supplier<T> defaultSupplier) {
        return this.get(k).orElseGet(defaultSupplier);
    }

    default <T> T getOrThrow(ContextKey<T> k) {
        return this.getOrThrow(k, () -> new NoSuchElementException("Context missing value for key " + k));
    }

    default <T, X extends Throwable> T getOrThrow(ContextKey<T> k, Supplier<X> exception) throws X {
        return this.get(k).orElseThrow(exception);
    }
}
