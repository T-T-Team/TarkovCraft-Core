package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public class ConfigToggleSkillTriggerCondition implements SkillTriggerCondition {

    public static final MapCodec<ConfigToggleSkillTriggerCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.namespace),
            Codec.STRING.fieldOf("field").forGetter(t -> t.path)
    ).apply(instance, ConfigToggleSkillTriggerCondition::new));

    private final String namespace;
    private final String path;

    public ConfigToggleSkillTriggerCondition(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    @Override
    public boolean isTriggerable(SkillContext context) {
        return Configuration.getConfig(this.namespace)
                .flatMap(holder -> holder.getValue(this.path, Boolean.class))
                .orElse(false);
    }

    @Override
    public Component getDescription() {
        Component title = Configuration.getConfig(this.namespace)
                .flatMap(holder -> holder.getConfigValue(this.path, Boolean.class))
                .map(IConfigValueReadable::getTitle)
                .orElseGet(() -> Component.literal("???"));
        return Component.translatable("skill.condition.config_toggle", title);
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.CONFIG_TOGGLE.get();
    }
}
