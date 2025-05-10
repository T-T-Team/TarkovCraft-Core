package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Configurable;

public class SkillSystemConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(localize = true, value = "Enables skill system")
    public boolean skillSystemEnabled = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(localize = true, value = "Allows you to lose skill experience (but not levels)")
    public boolean enableSkillExperienceLoss = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(localize = true, value = "Allows you to lose skill levels")
    public boolean enableSkillLevelLoss = true;
}
