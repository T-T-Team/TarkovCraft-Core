package tnt.tarkovcraft.core.util.helper;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

public final class Helper {

    public static <R> R orDefault(@Nullable R first, @NotNull R second) {
        return first == null ? Objects.requireNonNull(second) : first;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }
}
