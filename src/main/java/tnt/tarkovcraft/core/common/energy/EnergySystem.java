package tnt.tarkovcraft.core.common.energy;

import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;

public final class EnergySystem {

    public static boolean isEnabled() {
        SkillSystemConfig config = TarkovCraftCore.getConfig().skillSystemConfig;
        return config.skillSystemEnabled && config.staminaEnabled;
    }

    // TODO add events
}
