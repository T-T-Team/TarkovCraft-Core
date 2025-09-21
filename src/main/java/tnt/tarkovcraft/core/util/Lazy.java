package tnt.tarkovcraft.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {

    private Supplier<T> provider;
    private T value;

    private Lazy(Supplier<T> provider) {
        this.provider = provider;
    }

    public static <T> Lazy<T> of(Supplier<T> provider) {
        return new Lazy<>(provider);
    }

    public static <T> Lazy<T> of(T value) {
        return new Lazy<>(() -> value);
    }

    public static <T> Lazy<T> empty() {
        return new Lazy<>(null);
    }

    @Override
    public T get() {
        if (this.value != null) {
            return this.value;
        }
        if (this.provider == null) {
            return null;
        }
        this.value = this.provider.get();
        this.provider = null;
        return this.value;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (this.value != null) {
            consumer.accept(this.value);
        }
    }
}
