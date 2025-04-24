package tnt.tarkovcraft.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ObjectCache<K, V> {

    private final Map<K, V> cache;
    private final Function<K, V> compute;

    public ObjectCache(Function<K, V> compute) {
        this.cache = new HashMap<>();
        this.compute = compute;
    }

    public void invalidate() {
        this.cache.clear();
    }

    public boolean has(K key) {
        return this.cache.containsKey(key);
    }

    public V get(K key) {
        return this.cache.computeIfAbsent(key, this.compute);
    }
}
