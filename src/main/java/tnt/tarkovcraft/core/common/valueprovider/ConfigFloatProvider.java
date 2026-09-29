package tnt.tarkovcraft.core.common.valueprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.config.ConfigValueLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;

public final class ConfigFloatProvider extends AbstractConfigNumberProvider implements FloatProvider {

    public static final MapCodec<ConfigFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConfigValueLocation.CODEC.fieldOf("identifier").forGetter(AbstractConfigNumberProvider::identifier),
            Codec.FLOAT.optionalFieldOf("default", 0.0F).forGetter(AbstractConfigNumberProvider::fallbackValueAsFloat)
    ).apply(instance, ConfigFloatProvider::new));

    public ConfigFloatProvider(ConfigValueLocation identifier, float fallbackValue) {
        super(identifier, fallbackValue);
    }

    @Override
    public float sample(RandomSource random) {
        return this.getConfigValue().floatValue();
    }

    @Override
    public float min() {
        return -Float.MAX_VALUE;
    }

    @Override
    public float max() {
        return Float.MAX_VALUE;
    }

    @Override
    public MapCodec<? extends FloatProvider> codec() {
        return CODEC;
    }
}
