package tnt.tarkovcraft.core.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

public record UnitFormat(int decimalPlaces, double multiplier, Component suffix, RoundingMode roundingMode) {

    public static final UnitFormat IDENTITY = new UnitFormat(0, 1.0, CommonComponents.EMPTY, RoundingMode.ROUND);
    public static final UnitFormat PERCENT = new UnitFormat(2, 100.0, Component.literal("%"), RoundingMode.ROUND);
    public static final Codec<UnitFormat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("decimal_places", 2).forGetter(UnitFormat::decimalPlaces),
            Codec.DOUBLE.optionalFieldOf("multiplier", 1.0).forGetter(UnitFormat::multiplier),
            ComponentSerialization.CODEC.optionalFieldOf("suffix", CommonComponents.EMPTY).forGetter(UnitFormat::suffix),
            RoundingMode.CODEC.optionalFieldOf("rounding_mode", RoundingMode.ROUND).forGetter(UnitFormat::roundingMode)
    ).apply(instance, UnitFormat::new));

    public String format(double value) {
        if (Double.isNaN(value)) {
            return CommonLabels.NOT_AVAILABLE_SHORT.getString();
        }
        if (this.decimalPlaces == 0) {
            return String.format(Locale.ROOT, "%d%s", (int) this.roundingMode.applyAsDouble(value * multiplier), suffix.getString());
        } else {
            String format = "%." + decimalPlaces + "f%s";
            return String.format(Locale.ROOT, format, value * multiplier, suffix.getString());
        }
    }

    public enum RoundingMode implements DoubleUnaryOperator, StringRepresentable {

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
