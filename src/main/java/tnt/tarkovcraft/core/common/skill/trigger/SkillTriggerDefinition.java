package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.SkillContext;
import tnt.tarkovcraft.core.common.skill.trigger.condition.SkillTriggerCondition;
import tnt.tarkovcraft.core.common.skill.trigger.configuration.SkillTriggerConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SkillTriggerDefinition(SkillTrigger trigger, List<SkillTriggerCondition> conditions, SkillTriggerConfiguration configuration) {

    public static final Codec<SkillTriggerDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreRegistries.SKILL_TRIGGER_EVENT.byNameCodec().fieldOf("trigger").forGetter(SkillTriggerDefinition::trigger),
            SkillTriggerCondition.CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillTriggerDefinition::conditions),
            SkillTriggerConfiguration.CODEC.fieldOf("configuration").forGetter(SkillTriggerDefinition::configuration)
    ).apply(instance, SkillTriggerDefinition::new));

    public List<Component> getInfoComponents() {
        List<Component> infoComponents = new ArrayList<>();
        infoComponents.add(CommonComponents.space().append(this.trigger.getDisplayName()).withStyle(ChatFormatting.GREEN));
        if (!this.conditions.isEmpty()) {
            infoComponents.add(Component.literal("  ").append(Component.translatable("tooltip.tarkovcraft_core.skill.conditions")).withStyle(ChatFormatting.YELLOW));
            for (SkillTriggerCondition condition : this.conditions) {
                infoComponents.add(Component.literal("  - ").append(condition.getDescription()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        return infoComponents;
    }

    public boolean isTriggerable(SkillContext context) {
        for (SkillTriggerCondition condition : this.conditions) {
            if (!condition.isTriggerable(context))
                return false;
        }
        return this.configuration.canTrigger(context);
    }

    public float trigger(SkillContext context) {
        return this.configuration.getTriggerExperience(context);
    }
}
