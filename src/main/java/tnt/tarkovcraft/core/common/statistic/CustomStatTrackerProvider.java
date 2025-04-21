package tnt.tarkovcraft.core.common.statistic;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

public interface CustomStatTrackerProvider {

    default Holder<Statistic> getKillCounter(Entity killer) {
        return null;
    }

    default Holder<Statistic> getDeathCounter(Entity victim) {
        return null;
    }
}
