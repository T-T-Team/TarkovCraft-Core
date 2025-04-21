package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.statistic.Statistic;

public final class CoreStatistics {

    public static final DeferredRegister<Statistic> REGISTRY = DeferredRegister.create(CoreRegistries.STATISTICS, TarkovCraftCore.MOD_ID);

    public static final Holder<Statistic> KILLS = REGISTRY.register("kills", Statistic::new);
    public static final Holder<Statistic> PLAYER_KILLS = REGISTRY.register("player_kills", Statistic::new);
    public static final Holder<Statistic> DEATHS = REGISTRY.register("deaths", Statistic::new);
    public static final Holder<Statistic> PLAYER_DEATHS = REGISTRY.register("player_deaths", Statistic::new);
}
