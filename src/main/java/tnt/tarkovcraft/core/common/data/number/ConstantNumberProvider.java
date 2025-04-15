package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.init.NumberProviders;

public class ConstantNumberProvider implements NumberProvider {

    public static final MapCodec<ConstantNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, ConstantNumberProvider::new));
    public static final NumberProvider ZERO = new ConstantNumberProvider(0);
    public static final NumberProvider MAX_INT = new ConstantNumberProvider(Integer.MAX_VALUE);
    private final double value;

    public ConstantNumberProvider(double value) {
        this.value = value;
    }

    @Override
    public double getNumber() {
        return this.value;
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviders.CONSTANT.get();
    }
}
