package tnt.tarkovcraft.core.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Collection;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public enum LogicalOperator implements StringRepresentable {

    NOT("not") {
        @Override
        public <T> boolean apply(Collection<T> items, Predicate<T> filter) {
            if (items.size() != 1) {
                throw new IllegalArgumentException("NOT operator can only be used with a single item, input was " + items.size() + " items");
            }
            return !filter.test(items.iterator().next());
        }
    },

    OR("or") {
        @Override
        public <T> boolean apply(Collection<T> items, Predicate<T> filter) {
            return items.stream().anyMatch(filter);
        }
    },

    AND("and") {
        @Override
        public <T> boolean apply(Collection<T> items, Predicate<T> filter) {
            return items.stream().allMatch(filter);
        }
    };

    public static final EnumCodec<LogicalOperator> CODEC = StringRepresentable.fromEnum(LogicalOperator::values);
    public static final IntFunction<LogicalOperator> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, LogicalOperator> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    private final String serializedName;

    LogicalOperator(String serializedName) {
        this.serializedName = serializedName;
    }

    public abstract <T> boolean apply(Collection<T> items, Predicate<T> filter);

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}