package tnt.tarkovcraft.core.common.statistic;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;

public interface StatTrackerExtension {

    default Holder<Statistic> tarkovcraftCore$getKillCounter(Entity killer) {
        return null;
    }

    default Holder<Statistic> tarkovcraftCore$getDeathCounter(Entity victim) {
        return null;
    }
}
