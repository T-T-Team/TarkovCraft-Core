package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.NumberOperator;

import java.util.ArrayList;
import java.util.List;

public class OperandNumberProvider implements NumberProvider {

    public static final MapCodec<OperandNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodec(Codec.DOUBLE).listOf(2, Integer.MAX_VALUE).fieldOf("values")
                    .forGetter(t -> {
                        List<NumberProvider> values = t.values;
                        List<Either<NumberProvider, Either<Duration, Double>>> list = new ArrayList<>();
                        values.forEach(prov -> list.add(Either.left(prov)));
                        return list;
                    }),
            NumberOperator.CODEC.fieldOf("operator").forGetter(t -> t.operator)
    ).apply(instance, OperandNumberProvider::new));

    private final List<NumberProvider> values;
    private final NumberOperator operator;

    public OperandNumberProvider(List<Either<NumberProvider, Either<Duration, Double>>> values, NumberOperator operator) {
        this.values = values.stream().map(NumberProviderType::resolve).toList();
        this.operator = operator;
    }

    @Override
    public double getNumber() {
        double identity = this.values.getFirst().getNumber();
        return this.values.stream()
                .skip(1)
                .mapToDouble(NumberProvider::getNumber)
                .reduce(identity, this.operator);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.OPERAND.get();
    }
}
