package tnt.tarkovcraft.core.util.helper;

import org.jspecify.annotations.NonNull;

import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.Objects;

public final class Helper {

    public static <R> R orDefault(@Nullable R first, @NonNull R second) {
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

    public static <E extends Enum<E>> E nextEnum(E value) {
        E[] values = value.getDeclaringClass().getEnumConstants();
        int index = (value.ordinal() + 1) % values.length;
        return values[index];
    }

    public static <E extends Enum<E>> E previousEnum(E value) {
        E[] values = value.getDeclaringClass().getEnumConstants();
        int index = (value.ordinal() - 1 + values.length) % values.length;
        return values[index];
    }
}
