package tnt.tarkovcraft.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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

    public static <T> CompoundTag serialize(Codec<T> codec, T obj) {
        DataResult<Tag> result = codec.encodeStart(NbtOps.INSTANCE, obj);
        CompoundTag tag = new CompoundTag();
        tag.put("data", result.getOrThrow());
        return tag;
    }

    public static <T> T deserialize(Codec<T> codec, CompoundTag tag) {
        Tag data = tag.get("data");
        DataResult<T> result = codec.parse(NbtOps.INSTANCE, data);
        return result.getOrThrow();
    }
}
