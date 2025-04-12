package tnt.tarkovcraft.core.common.data.number;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public interface NumberProvider extends Supplier<Double>, DoubleSupplier {

    NumberProviderType<?> getType();

    double getNumber();

    @Override
    default Double get() {
        return getNumber();
    }

    @Override
    default double getAsDouble() {
        return getNumber();
    }
}
