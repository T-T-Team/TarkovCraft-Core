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

    @Configurable
    @Configurable.DecimalRange(min = 0.0, max = 100.0)
    @Configurable.Comment("Allows you to adjust skill leveling speed")
    @Configurable.Gui.NumberFormat("0.00")
    public float globalSkillLevelSpeedMultiplier = 1.0F;

    @Configurable
    @Configurable.DecimalRange(min = 0.0)
    @Configurable.Comment({"Max skill experience to be gained from single trigger - for example killing one mob, collecting one exp orb", "0 means no limit"})
    @Configurable.Gui.NumberFormat("0.00#")
    public float singleTriggerSkillLevelLimit = 0.0F;
}
