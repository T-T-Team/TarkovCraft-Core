package tnt.tarkovcraft.core.util.context;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.context.ContextKey;

import java.util.*;
import java.util.function.Function;

public class ContextImpl implements WritableContext {

    private static final ContextImpl EMPTY = new ContextImpl(ImmutableMap.of());

    private final Map<ContextKey<?>, Object> data;

    private ContextImpl(Map<ContextKey<?>, Object> data) {
        this.data = new HashMap<>(data);
    }

    public static ContextImpl empty() {
        return EMPTY;
    }

    public static Builder builder(int expectedSize) {
        return new Builder(expectedSize);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static <A> ContextImpl of(ContextKey<A> k1, A v1) {
        return builder(1).addProperty(k1, v1).build();
    }

    public static <A, B> ContextImpl of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2) {
        return builder(2).addProperty(k1, v1).addProperty(k2, v2).build();
    }

    public static <A, B, C> ContextImpl of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2, ContextKey<C> k3, C v3) {
        return builder(3).addProperty(k1, v1).addProperty(k2, v2).addProperty(k3, v3).build();
    }

    public static <A, B, C, D> ContextImpl of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2, ContextKey<C> k3, C v3, ContextKey<D> k4, D v4) {
        return builder(4).addProperty(k1, v1).addProperty(k2, v2).addProperty(k3, v3).addProperty(k4, v4).build();
    }

    public static <A, B, C, D, E> ContextImpl of(ContextKey<A> k1, A v1, ContextKey<B> k2, B v2, ContextKey<C> k3, C v3, ContextKey<D> k4, D v4, ContextKey<E> k5, E v5) {
        return builder(5).addProperty(k1, v1).addProperty(k2, v2).addProperty(k3, v3).addProperty(k4, v4).addProperty(k5, v5).build();
    }

    @Override
    public boolean containsKey(ContextKey<?> k) {
        return this.data.containsKey(k);
    }

    @Override
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
    @Override
    public <T> Optional<T> get(ContextKey<T> k) {
        return Optional.ofNullable((T) this.data.get(k));
    }

    @Override
    public Set<ContextKey<?>> listKeys() {
        return this.data.keySet();
    }

    @Override
    public <T> void set(ContextKey<T> key, T value) {
        this.data.put(key, value);
    }

    @Override
    public void remove(ContextKey<?> key) {
        this.data.remove(key);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    public void copy(Context reader) {
        Set<ContextKey<?>> keys = reader.listKeys();
        for (ContextKey<?> k : keys) {
            Object value = reader.get(k);
            this.data.put(k, value);
        }
    }

    public void copyMissing(Context reader) {
        Set<ContextKey<?>> keys = reader.listKeys();
        for (ContextKey<?> k : keys) {
            Object value = reader.get(k);
            this.data.putIfAbsent(k, value);
        }
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

        public Builder addFromSource(Context reader) {
            Set<ContextKey<?>> keys = reader.listKeys();
            for (ContextKey<?> k : keys) {
                this.data.put(k, reader.get(k));
            }
            return this;
        }

        public Builder addMissingFromSource(Context reader) {
            Set<ContextKey<?>> keys = reader.listKeys();
            for (ContextKey<?> k : keys) {
                this.data.putIfAbsent(k, reader.get(k));
            }
            return this;
        }

        public ContextImpl build() {
            return new ContextImpl(this.data);
        }
    }
}
