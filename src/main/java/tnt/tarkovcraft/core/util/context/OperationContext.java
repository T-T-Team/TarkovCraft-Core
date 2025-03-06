package tnt.tarkovcraft.core.util.context;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class OperationContext {

    private static final OperationContext EMPTY = new OperationContext(ImmutableMap.of());

    private final Map<ContextKey<?>, Object> data;

    private OperationContext(Map<ContextKey<?>, Object> data) {
        this.data = ImmutableMap.copyOf(data);
    }

    public static OperationContext empty() {
        return EMPTY;
    }

    public static Builder builder(int expectedSize) {
        return new Builder(expectedSize);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static <A> OperationContext of(ContextKey<A> k1, A v1) {
        return builder(1).addProperty(k1, v1).build();
    }

    public static <A, B> OperationContext of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2) {
        return builder(2).addProperty(k1, v1).addProperty(k2, v2).build();
    }

    public static <A, B, C> OperationContext of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2, ContextKey<C> k3, C v3) {
        return builder(3).addProperty(k1, v1).addProperty(k2, v2).addProperty(k3, v3).build();
    }

    public static <A, B, C, D> OperationContext of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2, ContextKey<C> k3, C v3, ContextKey<D> k4, D v4) {
        return builder(4).addProperty(k1, v1).addProperty(k2, v2).addProperty(k3, v3).addProperty(k4, v4).build();
    }

    public static <A, B, C, D, E> OperationContext of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2, ContextKey<C> k3, C v3, ContextKey<D> k4, D v4, ContextKey<E> k5, E v5) {
        return builder(5).addProperty(k1, v1).addProperty(k2, v2).addProperty(k3, v3).addProperty(k4, v4).addProperty(k5, v5).build();
    }

    public boolean containsKey(ContextKey<?> k) {
        return this.data.containsKey(k);
    }

    public boolean containsKeys(Collection<ContextKey<?>> keys) {
        return this.data.keySet().containsAll(keys);
    }

    public <X extends Throwable> void requireKeysOrThrow(Collection<ContextKey<?>> keys, Function<Collection<ContextKey<?>>, X> exception) throws X {
        Collection<ContextKey<?>> missing = new ArrayList<>(keys);
        missing.removeIf(this::containsKey);
        if (!missing.isEmpty()) {
            throw exception.apply(missing);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ContextKey<T> k) {
        return (T) this.data.get(k);
    }

    public <T> Optional<T> getOptional(ContextKey<T> k) {
        return Optional.ofNullable(this.get(k));
    }

    public <T> T getOrDefault(ContextKey<T> k, T defaultValue) {
        return this.getOptional(k).orElse(defaultValue);
    }

    public <T> T getOrThrow(ContextKey<T> k) {
        return this.getOrThrow(k, () -> new NoSuchElementException("Context missing value for key " + k.identifier()));
    }

    public <T, X extends Throwable> T getOrThrow(ContextKey<T> k, Supplier<X> exception) throws X {
        return this.getOptional(k).orElseThrow(exception);
    }

    public static final class Builder {

        private final Map<ContextKey<?>, Object> data;

        private Builder() {
            this.data = new HashMap<>();
        }

        private Builder(int expectedSize) {
            this.data = new HashMap<>(expectedSize);
        }

        public <T> Builder addProperty(ContextKey<T> key, T value) {
            this.data.put(Objects.requireNonNull(key, "key cannot be null"), Objects.requireNonNull(value, "value cannot be null"));
            return this;
        }

        public Builder removeProperty(ContextKey<?> key) {
            this.data.remove(key);
            return this;
        }

        public OperationContext build() {
            return new OperationContext(this.data);
        }
    }
}
