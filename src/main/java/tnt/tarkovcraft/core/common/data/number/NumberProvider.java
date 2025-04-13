package tnt.tarkovcraft.core.common.data.number;

import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

public interface NumberProvider extends Supplier<Double>, DoubleSupplier {

    NumberProviderType<?> getType();

    double getNumber();

    default int intValue() {
        return map(Double::intValue);
    }

    default float floatValue() {
        return map(Double::floatValue);
    }

    @Override
    default Double get() {
        return getNumber();
    }

    @Override
    default double getAsDouble() {
        return getNumber();
    }

    default <N extends Number> N map(Function<Double, N> mapper) {
        return mapper.apply(this.getNumber());
    }
}
