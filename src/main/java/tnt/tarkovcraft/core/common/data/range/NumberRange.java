package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

public record NumberRange(Mode mode, double from, double to) implements Range {

    public static final Codec<NumberRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.INCLUSIVE).forGetter(NumberRange::mode),
            NumberProvider.DOUBLE.fieldOf("from").forGetter(NumberRange::from),
            NumberProvider.DOUBLE.fieldOf("to").forGetter(NumberRange::to)
    ).apply(instance, NumberRange::new));

    public boolean isWithinRange(double input) {
        return isWithinRange(this.mode, input);
    }

    @Override
    public boolean isWithinRange(Mode mode, double input) {
        return mode.compare(input, this.from, this.to);
    }

    @Override
    public double getRandomInRange(RandomSource random) {
        return this.from + (this.to - this.from) * random.nextDouble();
    }
}
