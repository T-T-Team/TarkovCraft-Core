package tnt.tarkovcraft.core.util;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public final class Util {

    public static <R> R orDefault(@Nullable R first, @NotNull R second) {
        return first == null ? Objects.requireNonNull(second) : first;
    }
}
