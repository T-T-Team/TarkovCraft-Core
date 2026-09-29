package tnt.tarkovcraft.core.common.valueprovider;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigValueLocation;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;

import java.util.Optional;

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
}
