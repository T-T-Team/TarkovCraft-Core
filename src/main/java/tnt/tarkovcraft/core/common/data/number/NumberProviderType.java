package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.data.Duration;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

import java.util.Objects;
import java.util.function.Function;

public record NumberProviderType<N extends NumberProvider>(ResourceLocation identifier, MapCodec<N> codec) {

    public static final Codec<NumberProvider> ID_CODEC = TarkovCraftRegistries.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::getType, NumberProviderType::codec);

    // resolve either simple number, duration string or custom number provider
    public static <N extends Number> NumberProvider resolve(Either<N, Either<Duration, NumberProvider>> either) {
        return either.map(
                num -> new ConstantNumberProvider(num.doubleValue()),
                nested -> nested.map(
                        DurationNumberProvider::new,
                        Function.identity()
                )
        );
    }

    /**
     * Creates codec which supports definition of number provider as number / duration / custom number provider
     * @param numberCodec Number codec to be used for numbers
     * @return Constructed codec using the custom number codec
     * @param <N> Number type
     */
    public static <N extends Number> Codec<Either<N, Either<Duration, NumberProvider>>> complexCodec(Codec<N> numberCodec) {
        return Codec.either(numberCodec, Codec.either(Duration.STRING_CODEC, ID_CODEC));
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
