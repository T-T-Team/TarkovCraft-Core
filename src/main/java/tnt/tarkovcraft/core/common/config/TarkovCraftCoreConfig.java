package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.UpdateRestrictions;
import tnt.tarkovcraft.core.TarkovCraftCore;

@Config(id = TarkovCraftCore.MOD_ID, filename = "tarkovCraftCore", group = TarkovCraftCore.MOD_ID)
public class TarkovCraftCoreConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(localize = true, value = "Enables mail system for reception of items and chat messages")
    public boolean enableMailSystem = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(localize = true, value = "Allows messages to be sent between players as a chat")
    public boolean allowMailPlayerMessages = true;

    @Configurable
    @Configurable.UpdateRestriction(UpdateRestrictions.MAIN_MENU)
    @Configurable.Comment(localize = true, value = "Skill system tracker")
    public SkillSystemConfig skillSystemConfig = new SkillSystemConfig();
}
