package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;

public class ConfigurationNumberProvider implements NumberProvider {

    public static final MapCodec<ConfigurationNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.namespace),
            Codec.STRING.fieldOf("field").forGetter(t -> t.path),
            Codec.DOUBLE.optionalFieldOf("default", 0.0).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigurationNumberProvider::new));

    private final String namespace;
    private final String path;
    private final double defaultValue;

    public ConfigurationNumberProvider(String namespace, String path, double defaultValue) {
        this.namespace = namespace;
        this.path = path;
        this.defaultValue = defaultValue;
    }

    @Override
    public double getNumber() {
        return Configuration.getConfig(this.namespace)
                .flatMap(holder -> holder.getValue(this.path, Number.class))
                .map(Number::doubleValue)
                .orElse(this.defaultValue);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.CONFIG.get();
    }
}
