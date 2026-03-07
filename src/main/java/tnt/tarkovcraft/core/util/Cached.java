package tnt.tarkovcraft.core.util;

import java.util.Objects;
import java.util.function.Supplier;

public final class Cached<T> implements Supplier<T> {

    private final Supplier<T> provider;
    private T value;

    private Cached(Supplier<T> provider) {
        this.provider = provider;
    }

    public static <T> Cached<T> create(Supplier<T> provider) {
        return new Cached<>(Objects.requireNonNull(provider, "Provider cannot be null"));
    }

    @Override
    public T get() {
        if (this.value == null) {
            this.value = Objects.requireNonNull(this.provider.get(), "Cached value cannot be null");
        }
        return this.value;
    }

    public void invalidate() {
        this.value = null;
    }
}
