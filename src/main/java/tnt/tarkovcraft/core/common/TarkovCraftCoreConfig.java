package tnt.tarkovcraft.core.common;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import tnt.tarkovcraft.core.TarkovCraftCore;

@Config(id = TarkovCraftCore.MOD_ID, filename = "tarkovCraftCore", group = TarkovCraftCore.MOD_ID)
public class TarkovCraftCoreConfig {

    @Configurable
    @Configurable.Synchronized
    public boolean enableSkillSystem = true;
}
