package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Configurable;

public class SkillSystemConfig {

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Enables skill system")
    public boolean skillSystemEnabled = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Allows you to lose skill experience (but not levels)")
    @Configurable.DependsOn(
            configValues = @Configurable.DependsOn.ConfigValue(location = "tarkovcraft_core:skillSystemConfig/skillSystemEnabled", accepts = "true")
    )
    public boolean enableSkillExperienceLoss = true;

    @Configurable
    @Configurable.Synchronized
    @Configurable.Comment("Allows you to lose skill levels")
    @Configurable.DependsOn(
            configValues = {
                    @Configurable.DependsOn.ConfigValue(location = "tarkovcraft_core:skillSystemConfig/skillSystemEnabled", accepts = "true"),
                    @Configurable.DependsOn.ConfigValue(location = "tarkovcraft_core:skillSystemConfig/enableSkillExperienceLoss", accepts = "true")
            }
    )
    public boolean enableSkillLevelLoss = true;
}
