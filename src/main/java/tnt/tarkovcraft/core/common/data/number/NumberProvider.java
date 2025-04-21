package tnt.tarkovcraft.core.common.data.number;

import tnt.tarkovcraft.core.util.context.Context;

import java.util.function.Function;

public interface NumberProvider {

    NumberProviderType<?> getType();

    double getNumber(Context context);

    default double getNumber() {
        return this.getNumber(Context.NONE);
    }

    default int intValue(Context context) {
        return map(Double::intValue, context);
    }

    default int intValue() {
        return this.intValue(Context.NONE);
    }

    default float floatValue(Context context) {
        return map(Double::floatValue, context);
    }

    default float floatValue() {
        return this.floatValue(Context.NONE);
    }

    default <N extends Number> N map(Function<Double, N> mapper, Context context) {
        return mapper.apply(this.getNumber(context));
    }

    default <N extends Number> N map(Function<Double, N> mapper) {
        return this.map(mapper, Context.NONE);
    }
}
