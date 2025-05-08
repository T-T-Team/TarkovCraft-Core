package tnt.tarkovcraft.core.common.data.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

public class OperandNumberProvider implements NumberProvider {

    public static final MapCodec<OperandNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodec(Codec.DOUBLE).listOf(2, Integer.MAX_VALUE).fieldOf("values")
                    .forGetter(t -> {
                        List<NumberProvider> values = t.values;
                        List<Either<NumberProvider, Either<Duration, Double>>> list = new ArrayList<>();
                        values.forEach(prov -> list.add(Either.left(prov)));
                        return list;
                    }),
            Codecs.enumCodec(Operator.class).fieldOf("operator").forGetter(t -> t.operator)
    ).apply(instance, OperandNumberProvider::new));

    private final List<NumberProvider> values;
    private final Operator operator;

    public OperandNumberProvider(List<Either<NumberProvider, Either<Duration, Double>>> values, Operator operator) {
        this.values = values.stream().map(NumberProviderType::resolve).toList();
        this.operator = operator;
    }

    @Override
    public double getNumber(Context context) {
        double identity = this.values.getFirst().getNumber(context);
        return this.values.stream()
                .skip(1)
                .mapToDouble(prov -> prov.getNumber(context))
                .reduce(identity, this.operator);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.OPERAND.get();
    }

    public enum Operator implements DoubleBinaryOperator {

        ADD(Double::sum),
        SUB((l, r) -> l - r),
        MUL((l ,r) -> l * r),
        DIV((l ,r) -> l / r),
        MIN(Math::min),
        MAX(Math::max);

        private final DoubleBinaryOperator operator;

        Operator(DoubleBinaryOperator operator) {
            this.operator = operator;
        }

        @Override
        public double applyAsDouble(double left, double right) {
            return this.operator.applyAsDouble(left, right);
        }
    }
}
