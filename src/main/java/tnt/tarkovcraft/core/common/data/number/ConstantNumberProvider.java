package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ConstantNumberProvider implements NumberProvider {

    public static final MapCodec<ConstantNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, ConstantNumberProvider::new));

    private final double value;

    public ConstantNumberProvider(double value) {
        this.value = value;
    }

    public ConstantNumberProvider(Number number) {
        this(number.doubleValue());
    }

    public static ConstantNumberProvider of(double value) {
        return new ConstantNumberProvider(value);
    }

    @Override
    public double getNumber() {
        return this.value;
    }

    @Override
    public MapCodec<? extends NumberProvider> codec() {
        return CODEC;
    }
}
