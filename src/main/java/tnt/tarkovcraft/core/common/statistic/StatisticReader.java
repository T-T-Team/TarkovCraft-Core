package tnt.tarkovcraft.core.common.statistic;

import net.minecraft.core.Holder;

public interface StatisticReader {

    long get(Holder<Statistic> statistic);

    long getMinimum(Holder<Statistic> statistic, long minimum);
}
