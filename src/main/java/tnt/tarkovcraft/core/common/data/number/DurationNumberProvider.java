package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;
import tnt.tarkovcraft.core.util.context.Context;

public class DurationNumberProvider implements NumberProvider {

    public static final MapCodec<DurationNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Duration.STRING_CODEC.fieldOf("value").forGetter(t -> t.duration)
    ).apply(instance, DurationNumberProvider::new));

    private final Duration duration;

    public DurationNumberProvider(Duration duration) {
        this.duration = duration;
    }

    @Override
    public double getNumber(Context context) {
        return duration.tickValue();
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.DURATION.get();
    }
}
