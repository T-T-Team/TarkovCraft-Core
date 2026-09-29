package tnt.tarkovcraft.core.common.valueprovider;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigValueLocation;
import dev.toma.configuration.config.validate.NumberRange;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;
import dev.toma.configuration.config.value.INumericValue;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractConfigNumberProvider {

    private final ConfigValueLocation identifier;
    private final double fallbackValue;

    public AbstractConfigNumberProvider(ConfigValueLocation identifier, double fallbackValue) {
        this.identifier = identifier;
        this.fallbackValue = fallbackValue;
    }

    public final ConfigValueLocation identifier() {
        return this.identifier;
    }

    public final int fallbackValueAsInt() {
        return (int) this.fallbackValue;
    }

    public final float fallbackValueAsFloat() {
        return (float) this.fallbackValue;
    }

    protected final double fallbackValue() {
        return this.fallbackValue;
    }

    protected Number getConfigValue() {
        return this.getConfigValueHolder()
                .map(holder -> holder.get(IConfigValueReadable.Mode.SAVED))
                .orElse(this.fallbackValue);
    }

    protected final Optional<IConfigValue<Number>> getConfigValueHolder() {
        return Configuration.getConfigValueHolder(this.identifier, Number.class);
    }

    @SuppressWarnings("unchecked")
    protected <N extends Number & Comparable<N>> N resolveRangeValue(Function<NumberRange<N>, N> resolver, Supplier<N> defaultValue) {
        var valueHolder = this.getConfigValueHolder();
        return valueHolder.map(value -> {
            if (value instanceof INumericValue<?> numericValue) {
                NumberRange<N> range = (NumberRange<N>) numericValue.getRange();
                return resolver.apply(range);
            }
            return defaultValue.get();
        }).orElseGet(defaultValue);
    }
}
