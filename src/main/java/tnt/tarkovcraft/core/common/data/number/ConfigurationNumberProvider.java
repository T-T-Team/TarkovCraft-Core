package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigValueLocation;

public class ConfigurationNumberProvider implements NumberProvider {

    public static final MapCodec<ConfigurationNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConfigValueLocation.CODEC.fieldOf("location").forGetter(t -> t.location),
            Codec.DOUBLE.optionalFieldOf("default", 0.0).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigurationNumberProvider::new));

    private final ConfigValueLocation location;
    private final double defaultValue;

    public ConfigurationNumberProvider(ConfigValueLocation location, double defaultValue) {
        this.location = location;
        this.defaultValue = defaultValue;
    }

    @Override
    public double getNumber() {
        return Configuration.getConfigValue(this.location, Number.class)
                .map(Number::doubleValue)
                .orElse(this.defaultValue);
    }

    @Override
    public MapCodec<? extends NumberProvider> codec() {
        return CODEC;
    }
}
