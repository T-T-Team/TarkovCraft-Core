package tnt.tarkovcraft.core.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;

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
    public static final Codec<Integer> RGB_COLOR = Codec.withAlternative(ExtraCodecs.RGB_COLOR_CODEC, HEX_RGB_COLOR_CODEC);
    public static final Codec<Integer> NON_NEGATIVE_INT = Codec.intRange(0, Integer.MAX_VALUE);
    public static final Codec<Float> NON_NEGATIVE_FLOAT = Codec.floatRange(0.0F, Float.MAX_VALUE);

    public static <E extends Enum<E>> Codec<E> simpleEnumCodec(Class<E> type) {
        return enumCodec(type, Function.identity());
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> type) {
        return enumCodec(type, str -> str.toUpperCase(Locale.ROOT));
    }

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> type, Function<String, String> inputOp) {
        return Codec.STRING.comapFlatMap(str -> {
            try {
                return DataResult.success(Enum.valueOf(type, inputOp.apply(str)));
            } catch (IllegalArgumentException e) {
                return DataResult.error(() -> "Failed to parse enum value due to error " + e.getMessage());
            }
        }, Enum::name);
    }

    public static <R, T> R serialize(DynamicOps<R> ops, Codec<T> codec, T data) {
        DataResult<R> result = codec.encodeStart(ops, data);
        return result.getOrThrow();
    }

    public static <R, T> T deserialize(DynamicOps<R> ops, Codec<T> codec, R input) {
        DataResult<T> result = codec.parse(ops, input);
        return result.getOrThrow();
    }

    public static <T> CompoundTag serializeNbtCompound(Codec<T> codec, T obj) {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, obj);
        CompoundTag tag = new CompoundTag();
        tag.put("data", result.getOrThrow());
        return tag;
    }

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

    public static <E extends Enum<E>> Codec<Set<E>> enumSet(Codec<E> enumCodec) {
        return set(enumCodec, EnumSet::copyOf);
    }

    public static <E extends Enum<E>> Codec<Set<E>> enumSet(Class<E> enumType) {
        return enumSet(enumCodec(enumType));
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

    public static <T> T deserializeNbtCompound(Codec<T> codec, CompoundTag tag) {
        Tag data = tag.get("data");
        DataResult<T> result = codec.parse(NbtOps.INSTANCE, data);
        return result.getOrThrow();
    }

    public static <T> Codec<T> dynamicCodec(Encoder<T> encoder, Decoder<T> decoder) {
        return new Codec<T>() {
            @Override
            public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
                return decoder.decode(ops, input);
            }

            @Override
            public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
                return encoder.encode(input, ops, prefix);
            }
        };
    }
}
