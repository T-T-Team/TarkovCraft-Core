package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;

import java.util.Random;

public class RangedNumberProvider implements NumberProvider {

    public static final MapCodec<RangedNumberProvider> CODEC = RecordCodecBuilder.<RangedNumberProvider>mapCodec(instance -> instance.group(
            NumberProviderType.VALUE_CODEC.fieldOf("min").forGetter(t -> t.min),
            NumberProviderType.VALUE_CODEC.fieldOf("max").forGetter(t -> t.max)
    ).apply(instance, RangedNumberProvider::new));
    private static final Random RANDOM = new Random();

    private final NumberProvider min;
    private final NumberProvider max;

    public RangedNumberProvider(NumberProvider min, NumberProvider max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double getNumber() {
        double min = this.min.getNumber();
        double max = this.max.getNumber();
        return min + RANDOM.nextDouble() * (max - min);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.RANGED.get();
    }
}
