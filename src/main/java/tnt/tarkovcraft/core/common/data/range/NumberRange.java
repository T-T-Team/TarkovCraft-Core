package tnt.tarkovcraft.core.common.data.range;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;

public record NumberRange(Mode mode, NumberProvider from, NumberProvider to) implements Range {

    public static final Codec<NumberRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.INCLUSIVE).forGetter(NumberRange::mode),
            NumberProviderType.VALUE_CODEC.fieldOf("from").forGetter(NumberRange::from),
            NumberProviderType.VALUE_CODEC.fieldOf("to").forGetter(NumberRange::to)
    ).apply(instance, NumberRange::new));

    public boolean isWithinRange(double input) {
        return isWithinRange(this.mode, input);
    }

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
