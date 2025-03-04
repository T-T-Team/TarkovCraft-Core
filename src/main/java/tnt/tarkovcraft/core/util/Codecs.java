package tnt.tarkovcraft.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

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
}
