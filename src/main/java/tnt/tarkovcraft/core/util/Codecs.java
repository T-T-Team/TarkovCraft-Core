package tnt.tarkovcraft.core.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.time.ZonedDateTime;

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
