package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Random;

public class RangedNumberProvider implements NumberProvider {

    public static final MapCodec<RangedNumberProvider> CODEC = RecordCodecBuilder.<RangedNumberProvider>mapCodec(instance -> instance.group(
            NumberProvider.FLOAT.fieldOf("min").forGetter(t -> t.min),
            NumberProvider.FLOAT.fieldOf("max").forGetter(t -> t.max)
    ).apply(instance, RangedNumberProvider::new));
    private static final Random RANDOM = new Random();

    private final float min;
    private final float max;

    public RangedNumberProvider(float min, float max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double getNumber() {
        return this.min + RANDOM.nextFloat() * (this.max - this.min);
    }

    @Override
    public MapCodec<? extends NumberProvider> codec() {
        return CODEC;
    }
}
