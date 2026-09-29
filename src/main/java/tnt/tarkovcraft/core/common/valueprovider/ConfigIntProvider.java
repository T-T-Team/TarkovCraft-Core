package tnt.tarkovcraft.core.common.valueprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.config.ConfigValueLocation;
import dev.toma.configuration.config.validate.NumberRange;
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
        return this.resolveRangeValue(NumberRange::min, this::fallbackValueAsInt);
    }

    @Override
    public int maxInclusive() {
        return this.resolveRangeValue(NumberRange::max, this::fallbackValueAsInt);
    }

    @Override
    public MapCodec<? extends IntProvider> codec() {
        return CODEC;
    }
}
