package tnt.tarkovcraft.core.common.config;

import dev.toma.configuration.config.Configurable;

public class SkillSystemConfig {

    @Configurable
    @Configurable.Synchronized
    public boolean skillSystemEnabled = true;

    @Configurable
    @Configurable.Synchronized
    public boolean enableSkillLoss = true;
}
