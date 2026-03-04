package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;

public record TimeRange(NumberProvider from, NumberProvider to) implements Range {

    public static final Codec<TimeRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProviderType.CODEC.fieldOf("from").forGetter(TimeRange::from),
            NumberProviderType.CODEC.fieldOf("to").forGetter(TimeRange::to)
    ).apply(instance, TimeRange::new));

    @Override
    public boolean isWithinRange(Mode mode, double input) {
        return mode.compare(input, this.from.getNumber(), this.to.getNumber());
    }

    @Override
    public double getRandomInRange(RandomSource random) {
        double from = this.from.getNumber();
        double to = this.to.getNumber();
        return from + (to - from) * random.nextDouble();
    }
}
