package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

public record TimeRange(int from, int to) implements Range {

    public static final Codec<TimeRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProvider.NON_NEGATIVE_INT.fieldOf("from").forGetter(TimeRange::from),
            NumberProvider.NON_NEGATIVE_INT.fieldOf("to").forGetter(TimeRange::to)
    ).apply(instance, TimeRange::new));

    @Override
    public boolean isWithinRange(Mode mode, double input) {
        return mode.compare(input, this.from, this.to);
    }

    @Override
    public double getRandomInRange(RandomSource random) {
        return this.from + (this.to - this.from) * random.nextDouble();
    }
}
