package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.util.context.Context;

public class ConfigToggleSkillCondition implements SkillTriggerCondition {

    public static final MapCodec<ConfigToggleSkillCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("configId").forGetter(t -> t.configId),
            Codec.STRING.fieldOf("configPath").forGetter(t -> t.configPath)
    ).apply(instance, ConfigToggleSkillCondition::new));

    private final String configId;
    private final String configPath;

    public ConfigToggleSkillCondition(String configId, String configPath) {
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
