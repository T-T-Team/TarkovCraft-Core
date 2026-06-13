package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.function.Function;
import java.util.function.Predicate;

public interface NumberProvider {

    Codec<NumberProvider> BASE_CODEC = CoreRegistries.NUMBER_PROVIDER.byNameCodec().dispatch(NumberProvider::codec, Function.identity());
    Codec<Integer> INT = codec(Codec.INT, NumberProvider::intValue);
    Codec<Integer> POSITIVE_INT = codec(Codec.INT, NumberProvider::intValue, integer -> integer > 0);
    Codec<Integer> NON_NEGATIVE_INT = codec(Codec.INT, NumberProvider::intValue, integer -> integer >= 0);
    Codec<Long> LONG = codec(Codec.LONG, NumberProvider::longValue);
    Codec<Long> POSITIVE_LONG = codec(Codec.LONG, NumberProvider::longValue, longValue -> longValue > 0L);
    Codec<Long> NON_NEGATIVE_LONG = codec(Codec.LONG, NumberProvider::longValue, longValue -> longValue >= 0L);
    Codec<Float> FLOAT = codec(Codec.FLOAT, NumberProvider::floatValue);
    Codec<Float> POSITIVE_FLOAT = codec(Codec.FLOAT, NumberProvider::floatValue, aFloat -> aFloat > 0);
    Codec<Float> NON_NEGATIVE_FLOAT = codec(Codec.FLOAT, NumberProvider::floatValue, aFloat -> aFloat >= 0);
    Codec<Float> PERCENT = codec(Codec.floatRange(0.0F, 1.0F), prov -> Mth.clamp(prov.floatValue(), 0.0F, 1.0F));
    Codec<Double> DOUBLE = codec(Codec.DOUBLE, NumberProvider::getNumber);
    Codec<Double> POSITIVE_DOUBLE = codec(Codec.DOUBLE, NumberProvider::getNumber, aDouble -> aDouble > 0.0);
    Codec<Double> NON_NEGATIVE_DOUBLE = codec(Codec.DOUBLE, NumberProvider::getNumber, aDouble -> aDouble >= 0.0);
    Codec<Integer> DURATION = Codec.either(NON_NEGATIVE_INT, Duration.STRING_CODEC)
            .xmap(v -> v.map(Function.identity(), Duration::tickValue), Either::left);

    static <N extends Number> Codec<N> codec(Codec<N> codec, Function<NumberProvider, N> toNumber) {
        return codec(codec, toNumber, n -> true);
    }

    static <N extends Number> Codec<N> codec(Codec<N> codec, Function<NumberProvider, N> toNumber, Predicate<N> validator) {
        return Codec.either(codec, BASE_CODEC)
                .xmap(v -> v.map(Function.identity(), toNumber), Either::left)
                .validate(n -> {
                    if (!validator.test(n)) {
                        return DataResult.error(() -> "Invalid number input: " + n);
                    }
                    return DataResult.success(n);
                });
    }

    MapCodec<? extends NumberProvider> codec();

    double getNumber();

    default int intValue() {
        return map(Double::intValue);
    }

    default long longValue() {
        return map(Double::longValue);
    }

    default float floatValue() {
        return map(Double::floatValue);
    }

    default <N extends Number> N map(Function<Double, N> mapper) {
        return mapper.apply(this.getNumber());
    }
}
