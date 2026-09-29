package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public record IntRange(IntProvider from, IntProvider to) implements Range {

    public static final Codec<IntRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProviders.CODEC.fieldOf("from").forGetter(IntRange::from),
            IntProviders.CODEC.fieldOf("to").forGetter(IntRange::to)
    ).apply(instance, IntRange::new));

    @Override
    public boolean isWithinRange(Mode mode, float input) {
        return mode.compare(input, this.from.minInclusive(), this.to.maxInclusive());
    }

    @Override
    public float getRandomInRange(RandomSource random) {
        int i0 = this.from.sample(random);
        int i1 = this.to.sample(random);
        return i0 + Mth.floor((i1 - i0) * random.nextFloat());
    }
}
