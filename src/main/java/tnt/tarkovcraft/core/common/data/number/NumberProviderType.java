package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;
import java.util.function.Function;

public record NumberProviderType<N extends NumberProvider>(Identifier identifier, MapCodec<N> codec) {

    public static final Codec<NumberProvider> ID_CODEC = CoreRegistries.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::getType, NumberProviderType::codec);
    public static final Codec<NumberProvider> VALUE_CODEC = valueCodec(Codec.DOUBLE);
    public static final Codec<NumberProvider> CODEC = durationCodec(ExtraCodecs.NON_NEGATIVE_FLOAT);

    public static <N extends Number> Codec<NumberProvider> valueCodec(Codec<N> codec) {
        return Codec.either(ID_CODEC, codec)
                .xmap(v -> v.map(Function.identity(), ConstantNumberProvider::new), Either::left);
    }

    public static <N extends Number> Codec<NumberProvider> durationCodec(Codec<N> codec) {
        return Codec.either(valueCodec(codec), Duration.STRING_CODEC)
                .xmap(v -> v.map(Function.identity(), DurationNumberProvider::new), Either::left);
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
