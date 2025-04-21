package tnt.tarkovcraft.core.common.data.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.init.CoreNumberProviders;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.statistic.Statistic;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.util.context.Context;

public class StatisticNumberProvider implements NumberProvider {

    public static final MapCodec<StatisticNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CoreRegistries.STATISTICS.byNameCodec().fieldOf("stat").forGetter(t -> t.stat)
    ).apply(instance, StatisticNumberProvider::new));
    private final Statistic stat;

    public StatisticNumberProvider(Statistic stat) {
        this.stat = stat;
    }

    @Override
    public double getNumber(Context context) {
        return context.get(StatisticTracker.TRACKER).map(tracker -> tracker.get(this.stat))
                .orElse(0L);
    }

    @Override
    public NumberProviderType<?> getType() {
        return CoreNumberProviders.STATISTIC.get();
    }
}
