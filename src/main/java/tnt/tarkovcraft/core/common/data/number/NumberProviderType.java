package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;
import java.util.function.Function;

public record NumberProviderType<N extends NumberProvider>(ResourceLocation identifier, MapCodec<N> codec) {

    public static final Codec<NumberProvider> ID_CODEC = CoreRegistries.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::getType, NumberProviderType::codec);

    // resolve either simple number, duration string or custom number provider
    public static <N extends Number> NumberProvider resolve(Either<NumberProvider, Either<Duration, N>> either) {
        return either.map(
                Function.identity(),
                nested -> nested.map(
                        DurationNumberProvider::new,
                        num -> new ConstantNumberProvider(num.doubleValue())
                )
        );
    }

    public static <N extends Number> NumberProvider resolveNoDuration(Either<NumberProvider, N> either) {
        return either.map(
                Function.identity(),
                num -> new ConstantNumberProvider(num.doubleValue())
        );
    }

    /**
     * Creates codec which supports definition of number provider as number / duration / custom number provider
     * @param numberCodec Number codec to be used for numbers
     * @return Constructed codec using the custom number codec
     * @param <N> Number type
     */
    public static <N extends Number> Codec<Either<NumberProvider, Either<Duration, N>>> complexCodec(Codec<N> numberCodec) {
        return Codec.either(ID_CODEC, Codec.either(Duration.STRING_CODEC, numberCodec));
    }

    public static <N extends Number> Codec<Either<NumberProvider, N>> complexCodecNoDuration(Codec<N> numberCodec) {
        return Codec.either(ID_CODEC, numberCodec);
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
