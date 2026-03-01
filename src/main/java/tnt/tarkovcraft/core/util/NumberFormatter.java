package tnt.tarkovcraft.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public record NumberFormatter(String format, List<Component> args, double multiplier, RoundingMode numberRounding) {

    public static final Codec<NumberFormatter> CODEC = Codec.withAlternative(
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("format").forGetter(NumberFormatter::format),
                    ComponentSerialization.CODEC.listOf().optionalFieldOf("arguments", Collections.emptyList()).forGetter(NumberFormatter::args),
                    Codec.DOUBLE.optionalFieldOf("multiplier", 1.0).forGetter(NumberFormatter::multiplier),
                    RoundingMode.CODEC.optionalFieldOf("rounding", RoundingMode.NONE).forGetter(NumberFormatter::numberRounding)
            ).apply(instance, NumberFormatter::new)),
            Codec.STRING,
            value -> new NumberFormatter(value, Collections.emptyList(), 1.0, RoundingMode.NONE)
    );

    public static final NumberFormatter IDENTITY = new NumberFormatter("%f", Collections.emptyList(), 1.0, RoundingMode.ROUND);

    public String formatValue(double value) {
        double adjustedValue = this.numberRounding.applyAsDouble(value * this.multiplier);
        Object[] argArray = new Object[this.args.size() + 1];
        argArray[0] = adjustedValue;
        System.arraycopy(this.args.toArray(), 0, argArray, 1, this.args.size());
        return String.format(this.format, argArray);
    }

    public enum RoundingMode implements DoubleUnaryOperator, StringRepresentable {

        NONE("none", DoubleUnaryOperator.identity()),
        FLOOR("floor", Mth::floor),
        ROUND("round", Math::round),
        CEIL("ceil", Mth::ceil);

        public static final EnumCodec<RoundingMode> CODEC = StringRepresentable.fromEnum(RoundingMode::values);

        private final String serializedName;
        private final DoubleUnaryOperator operator;

        RoundingMode(String serializedName, DoubleUnaryOperator operator) {
            this.serializedName = serializedName;
            this.operator = operator;
        }

        @Override
        public String getSerializedName() {
            return serializedName;
        }

        @Override
        public double applyAsDouble(double operand) {
            return this.operator.applyAsDouble(operand);
        }
    }
}
