package tnt.tarkovcraft.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import tnt.tarkovcraft.core.util.helper.ARGB;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

public final class Codecs {

    public static final Codec<ZonedDateTime> ZONED_DATE_TIME_CODEC = Codec.STRING.comapFlatMap(
            raw -> {
                try {
                    ZonedDateTime zdt = ZonedDateTime.parse(raw);
                    return DataResult.success(zdt);
                } catch (Exception e) {
                    return DataResult.error(() -> "Failed to parse ZonedDateTime due to error " + e.getMessage());
                }
            },
            ZonedDateTime::toString
    );
    public static final Codec<Integer> HEX_RGB_COLOR_CODEC = Codec.STRING.comapFlatMap(string -> {
        try {
            int value = Integer.decode(string);
            return DataResult.success(value & 0xFFFFFF);
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Failed to parse hex color due to error " + e.getMessage());
        }
    }, Integer::toHexString);
    public static final Codec<Integer> RGB_COLOR_CODEC = Codec.withAlternative(
            Codec.INT, ExtraCodecs.VECTOR3F, p_370488_ -> ARGB.colorFromFloat(1.0F, p_370488_.x(), p_370488_.y(), p_370488_.z())
    );
    public static final Codec<Integer> RGB_COLOR = Codec.withAlternative(RGB_COLOR_CODEC, HEX_RGB_COLOR_CODEC);
    public static final Codec<Integer> NON_NEGATIVE_INT = Codec.intRange(0, Integer.MAX_VALUE);
    public static final Codec<Float> NON_NEGATIVE_FLOAT = Codec.floatRange(0.0F, Float.MAX_VALUE);

    public static final StreamCodec<ByteBuf, Long> LONG_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Long decode(ByteBuf buffer) {
            return buffer.readLong();
        }

        @Override
        public void encode(ByteBuf buffer, Long value) {
            buffer.writeLong(value);
        }
    };
    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new
    );

    public static <T> Codec<List<T>> list(Codec<T> elementCodec, int minCount, int maxCount) {
        return Codec.withAlternative(elementCodec.listOf(minCount, maxCount), elementCodec, Collections::singletonList);
    }

    public static <T> Codec<List<T>> lowerBoundList(Codec<T> elementCodec, int minCount) {
        return list(elementCodec, minCount, Integer.MAX_VALUE);
    }

    public static <T> Codec<List<T>> upperBoundList(Codec<T> elementCodec, int maxCount) {
        return list(elementCodec, 0, maxCount);
    }

    public static <T> Codec<List<T>> list(Codec<T> elementCodec) {
        return list(elementCodec, 0, Integer.MAX_VALUE);
    }

    public static <T> Codec<Set<T>> hashSet(Codec<T> elementCodec) {
        return set(elementCodec, HashSet::new);
    }

    public static <T> Codec<Set<T>> linkedHashSet(Codec<T> elementCodec) {
        return set(elementCodec, LinkedHashSet::new);
    }

    public static <E extends Enum<E> & StringRepresentable> Codec<Set<E>> enumSet(Codec<E> enumCodec) {
        return set(enumCodec, EnumSet::copyOf);
    }

    public static <T> Codec<Set<T>> set(Codec<T> codec, Function<List<T>, Set<T>> setProvider) {
        return list(codec).xmap(setProvider, ArrayList::new);
    }

    public static <T, C extends Collection<T>> Codec<C> collection(Codec<T> codec, int minCount, int maxCount, Function<List<T>, C> toCollection, Function<C, List<T>> fromCollection) {
        return list(codec, minCount, maxCount).xmap(toCollection, fromCollection);
    }

    public static <T, C extends Collection<T>> Codec<C> lowerBoundCollection(Codec<T> codec, int minCount, Function<List<T>, C> toCollection, Function<C, List<T>> fromCollection) {
        return lowerBoundList(codec, minCount).xmap(toCollection, fromCollection);
    }

    public static <T, C extends Collection<T>> Codec<C> upperBoundCollection(Codec<T> codec, int maxCount, Function<List<T>, C> toCollection, Function<C, List<T>> fromCollection) {
        return upperBoundList(codec, maxCount).xmap(toCollection, fromCollection);
    }

    public static <T, C extends Collection<T>> Codec<C> collection(Codec<T> codec, Function<List<T>, C> toCollection, Function<C, List<T>> fromCollection) {
        return collection(codec, 0, Integer.MAX_VALUE, toCollection, fromCollection);
    }
}
