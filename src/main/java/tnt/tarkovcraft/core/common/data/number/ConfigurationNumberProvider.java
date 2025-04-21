package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.value.IConfigValueReadable;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;
import tnt.tarkovcraft.core.util.context.Context;

public class ConfigurationNumberProvider implements NumberProvider {

    public static final MapCodec<ConfigurationNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.configId),
            Codec.STRING.fieldOf("field").forGetter(t -> t.fieldId),
            Codec.DOUBLE.optionalFieldOf("default", 0.0).forGetter(t -> t.defaultValue)
    ).apply(instance, ConfigurationNumberProvider::new));

    private final String configId;
    private final String fieldId;
    private final double defaultValue;

    public ConfigurationNumberProvider(String configId, String fieldId, double defaultValue) {
        this.configId = configId;
        this.fieldId = fieldId;
        this.defaultValue = defaultValue;
    }

    @Override
    public double getNumber(Context context) {
        return Configuration.getConfig(this.configId)
                .flatMap(holder -> holder.getConfigValue(this.fieldId, Number.class))
                .map(val -> {
                    Number number = val.get(IConfigValueReadable.Mode.SAVED);
                    return number.doubleValue();
                }).orElse(this.defaultValue);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.CONFIG.get();
    }
}
