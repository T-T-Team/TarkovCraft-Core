package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.util.context.Context;

public class ConfigToggleSkillTriggerCondition implements SkillTriggerCondition {

    public static final MapCodec<ConfigToggleSkillTriggerCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("configId").forGetter(t -> t.configId),
            Codec.STRING.fieldOf("configPath").forGetter(t -> t.configPath)
    ).apply(instance, ConfigToggleSkillTriggerCondition::new));

    private final String configId;
    private final String configPath;

    public ConfigToggleSkillTriggerCondition(String configId, String configPath) {
        this.configId = configId;
        this.configPath = configPath;
    }

    @Override
    public boolean isTriggerable(Context context) {
        return Configuration.getConfig(this.configId).flatMap(holder -> holder.getConfigValue(this.configPath, Boolean.class))
                .map(value -> value.get(IConfigValueReadable.Mode.SAVED))
                .orElse(false);
    }

    @Override
    public Component getDescription() {
        Component title = Configuration.getConfig(this.configId).flatMap(holder -> holder.getConfigValue(this.configPath, Boolean.class))
                .map(IConfigValueReadable::getTitle)
                .orElse(Component.literal("???"));
        return Component.translatable("skill.condition.config_toggle", title);
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.CONFIG_TOGGLE.get();
    }
}
