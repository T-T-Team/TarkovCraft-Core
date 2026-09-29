package tnt.tarkovcraft.core.common.valueprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.config.ConfigValueLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;

public final class ConfigIntProvider extends AbstractConfigNumberProvider implements IntProvider {

    public static final MapCodec<ConfigIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConfigValueLocation.CODEC.fieldOf("identifier").forGetter(AbstractConfigNumberProvider::identifier),
            Codec.INT.optionalFieldOf("default", 0).forGetter(AbstractConfigNumberProvider::fallbackValueAsInt)
    ).apply(instance, ConfigIntProvider::new));

    public ConfigIntProvider(ConfigValueLocation identifier, int fallbackValue) {
        super(identifier, fallbackValue);
    }

    @Override
    public int sample(RandomSource random) {
        return this.getConfigValue().intValue();
    }

    @Override
    public int minInclusive() {
        return Integer.MIN_VALUE; // TODO work with the @Range attribute
    }

    @Override
    public int maxInclusive() {
        return Integer.MAX_VALUE; // TODO work with the @Range attribute
    }

    @Override
    public MapCodec<? extends IntProvider> codec() {
        return CODEC;
    }
}
