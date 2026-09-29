package tnt.tarkovcraft.core.common.valueprovider;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import tnt.tarkovcraft.core.common.data.duration.Duration;

public record DurationIntProvider(Duration value) implements IntProvider {

    public static final MapCodec<DurationIntProvider> CODEC = Duration.STRING_CODEC
            .xmap(DurationIntProvider::new, DurationIntProvider::value).fieldOf("value");

    @Override
    public int sample(RandomSource random) {
        return this.value.tickValue();
    }

    @Override
    public int minInclusive() {
        return this.value.tickValue();
    }

    @Override
    public int maxInclusive() {
        return this.value.tickValue();
    }

    @Override
    public MapCodec<? extends IntProvider> codec() {
        return CODEC;
    }
}
