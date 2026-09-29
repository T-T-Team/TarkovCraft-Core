package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;

public record FloatRange(Mode mode, FloatProvider from, FloatProvider to) implements Range {

    public static final Codec<FloatRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.INCLUSIVE).forGetter(FloatRange::mode),
            FloatProviders.CODEC.fieldOf("from").forGetter(FloatRange::from),
            FloatProviders.CODEC.fieldOf("to").forGetter(FloatRange::to)
    ).apply(instance, FloatRange::new));

    public boolean isWithinRange(float input) {
        return isWithinRange(this.mode, input);
    }

    @Override
    public boolean isWithinRange(Mode mode, float input) {
        return mode.compare(input, this.from.min(), this.to.max());
    }

    @Override
    public float getRandomInRange(RandomSource random) {
        float f0 = this.from.sample(random);
        float f1 = this.from.sample(random);
        return f0 + (f1 - f0) * random.nextFloat();
    }
}
