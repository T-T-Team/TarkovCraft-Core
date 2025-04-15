package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;

import java.util.Locale;
import java.util.Random;

public class RangedNumberProvider implements NumberProvider {

    public static final MapCodec<RangedNumberProvider> CODEC = RecordCodecBuilder.<RangedNumberProvider>mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("min").forGetter(t -> t.min),
            Codec.DOUBLE.fieldOf("max").forGetter(t -> t.max)
    ).apply(instance, RangedNumberProvider::new)).validate(provider -> {
        if (provider.min < provider.max) {
            return DataResult.error(() -> String.format(Locale.ROOT, "Max value {%f} is greater than min {%f}", provider.max, provider.min));
        }
        return DataResult.success(provider);
    });
    private static final Random RANDOM = new Random();

    private final double min;
    private final double max;

    public RangedNumberProvider(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double getNumber() {
        return this.min + RANDOM.nextDouble() * (this.max - this.min);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.RANGED.get();
    }
}
