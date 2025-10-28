package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Configurable;

public class SkillSystemConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(value = "Enables skill system", localize = true)
    public boolean skillSystemEnabled = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(value = "Allows you to lose skill experience (but not levels)", localize = true)
    public boolean enableSkillExperienceLoss = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment(value = "Allows you to lose skill levels", localize = true)
    public boolean enableSkillLevelLoss = true;
}
