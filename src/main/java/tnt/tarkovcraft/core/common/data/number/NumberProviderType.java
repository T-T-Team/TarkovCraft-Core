package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;
import java.util.function.Function;

public record NumberProviderType<N extends NumberProvider>(Identifier identifier, MapCodec<N> codec) {

    public static final Codec<NumberProvider> ID_CODEC = CoreRegistries.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::getType, NumberProviderType::codec);
    public static final Codec<NumberProvider> VALUE_CODEC = valueCodec(Codec.DOUBLE);
    public static final Codec<NumberProvider> CODEC = durationCodec(ExtraCodecs.NON_NEGATIVE_FLOAT);
    public static final Codec<Integer> INT_CODEC = numberCodec(Codec.INT, NumberProvider::intValue);
    public static final Codec<Long> LONG_CODEC = numberCodec(Codec.LONG, NumberProvider::longValue);
    public static final Codec<Float> FLOAT_CODEC = numberCodec(Codec.FLOAT, NumberProvider::floatValue);
    public static final Codec<Double> DOUBLE_CODEC = numberCodec(Codec.DOUBLE, NumberProvider::getNumber);
    public static final Codec<Float> PERCENT_CODEC = numberCodec(Codec.floatRange(0.0F, 1.0F), prov -> Mth.clamp(prov.floatValue(), 0.0F, 1.0F));

    public static <N extends Number> Codec<NumberProvider> valueCodec(Codec<N> codec) {
        return Codec.either(ID_CODEC, codec)
                .xmap(v -> v.map(Function.identity(), ConstantNumberProvider::new), Either::left);
    }

    public static <N extends Number> Codec<NumberProvider> durationCodec(Codec<N> codec) {
        return Codec.either(valueCodec(codec), Duration.STRING_CODEC)
                .xmap(v -> v.map(Function.identity(), DurationNumberProvider::new), Either::left);
    }

    public static <N extends Number> Codec<N> numberCodec(Codec<N> codec, Function<NumberProvider, N> toNumber) {
        return Codec.either(codec, ID_CODEC)
                .xmap(v -> v.map(Function.identity(), toNumber), Either::left);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NumberProviderType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
