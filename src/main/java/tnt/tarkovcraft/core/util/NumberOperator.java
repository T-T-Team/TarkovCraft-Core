package tnt.tarkovcraft.core.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.DoubleBinaryOperator;
import java.util.function.IntFunction;

public enum NumberOperator implements DoubleBinaryOperator, StringRepresentable {

    ADD("add", Double::sum),
    SUBTRACT("subtract", (l, r) -> l - r),
    MULTIPLY("multiply", (l, r) -> l * r),
    DIVIDE("divide", (l, r) -> l / r),
    POW("pow", Math::pow),
    POW_INV("pow_inv", (l, r) -> Math.pow(r, l)),
    MIN("min", Math::min),
    MAX("max", Math::max);

    public static final EnumCodec<NumberOperator> CODEC = StringRepresentable.fromEnum(NumberOperator::values);
    public static final IntFunction<NumberOperator> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, NumberOperator> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);

    private final String name;
    private final DoubleBinaryOperator operator;

    NumberOperator(String name, DoubleBinaryOperator operator) {
        this.name = name;
        this.operator = operator;
    }

    @Override
    public double applyAsDouble(double left, double right) {
        return this.operator.applyAsDouble(left, right);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
