package tnt.tarkovcraft.core.util.context;

import net.minecraft.util.context.ContextKey;

public interface WritableContext extends Context {

    <T> void set(ContextKey<T> key, T value);

    void remove(ContextKey<?> key);

    void clear();
}
