package tnt.tarkovcraft.core.util.context;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Predicate;

public record ContextKey<T>(ResourceLocation identifier, Class<T> type) implements Predicate<Object> {

    @Override
    public boolean test(Object o) {
        return this.type.isInstance(o);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContextKey<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
