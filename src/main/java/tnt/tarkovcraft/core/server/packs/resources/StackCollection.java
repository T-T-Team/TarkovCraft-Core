package tnt.tarkovcraft.core.server.packs.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public record StackCollection<T>(boolean replace, List<T> values) implements Iterable<T> {

    public boolean isEmpty() {
        return !this.replace && this.values.isEmpty();
    }

    public StackCollection<T> merge(StackCollection<T> other) {
        if (other.replace()) {
            return other;
        }
        List<T> mergedList = new ArrayList<>(this.values);
        mergedList.addAll(other.values);
        return new StackCollection<>(false, mergedList);
    }

    public static <T> StackCollection<T> empty() {
        return new StackCollection<>(false, Collections.emptyList());
    }

    public static <T> Codec<StackCollection<T>> codec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(StackCollection::replace),
                elementCodec.listOf().fieldOf("values").forGetter(StackCollection::values)
        ).apply(instance, StackCollection::new));
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.values.iterator();
    }
}
