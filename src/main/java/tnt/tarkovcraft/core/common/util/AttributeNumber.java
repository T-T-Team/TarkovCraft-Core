package tnt.tarkovcraft.core.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Optional;

public record AttributeNumber(Optional<Double> constant, Optional<Attribute> attribute) {

    public static final Codec<AttributeNumber> CODEC = codec(Codec.DOUBLE);

    public static <N extends Number> Codec<AttributeNumber> codec(Codec<Double> codec) {
        return RecordCodecBuilder.<AttributeNumber>create(instance -> instance.group(
                codec.optionalFieldOf("constant").forGetter(AttributeNumber::constant),
                CoreRegistries.ATTRIBUTE.byNameCodec().optionalFieldOf("attribute").forGetter(AttributeNumber::attribute)
        ).apply(instance, AttributeNumber::new)).validate(an -> {
            if (an.constant.isEmpty() && an.attribute.isEmpty()) {
                return DataResult.error(() -> "Either constant or attribute must be specified");
            }
            return DataResult.success(an);
        });
    }

    public double getValue(EntityAttributeData data) {
        return this.attribute.map(att -> data.getAttribute(att).value())
                .or(this::constant)
                .orElseThrow();
    }
}
