package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import tnt.tarkovcraft.core.TarkovCraftCore;

@Config(id = TarkovCraftCore.MOD_ID, filename = "tarkovCraftCore", group = TarkovCraftCore.MOD_ID)
public class TarkovCraftCoreConfig {

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.MAIN_MENU)
    @Configurable.Comment(value = "Configurations related to skill system", localize = true)
    public SkillSystemConfig skillSystemConfig = new SkillSystemConfig();

    @Configurable
    @Configurable.Comment(value = "Configurations related to weight system", localize = true)
    public WeightConfig weightConfig = new WeightConfig();
}
