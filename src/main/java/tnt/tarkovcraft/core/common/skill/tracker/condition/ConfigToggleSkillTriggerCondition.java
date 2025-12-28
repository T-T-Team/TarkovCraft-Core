package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigValueLocation;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public record ConfigToggleSkillTriggerCondition(ConfigValueLocation location) implements SkillTriggerCondition {

    public static final MapCodec<ConfigToggleSkillTriggerCondition> CODEC = ConfigValueLocation.CODEC
            .xmap(ConfigToggleSkillTriggerCondition::new, ConfigToggleSkillTriggerCondition::location).fieldOf("location");

    @Override
    public boolean isTriggerable(SkillContext context) {
        return Configuration.getConfigValue(this.location, Boolean.class)
                .orElse(false);
    }

    @Override
    public Component getDescription() {
        Component title = Configuration.getConfigValueHolder(this.location, Boolean.class)
                .map(IConfigValueReadable::getTitle)
                .orElse(Component.literal("???"));
        return Component.translatable("skill.condition.config_toggle", title);
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.CONFIG_TOGGLE.get();
    }
}
