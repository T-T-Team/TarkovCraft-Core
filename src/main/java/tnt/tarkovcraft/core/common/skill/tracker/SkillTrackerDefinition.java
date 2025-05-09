package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.tracker.condition.SkillTriggerCondition;
import tnt.tarkovcraft.core.common.skill.tracker.condition.SkillTriggerConditionType;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SkillTrackerDefinition(SkillTriggerEvent event, List<SkillTriggerCondition> conditions, SkillTracker tracker) {

    public static final Codec<SkillTrackerDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreRegistries.SKILL_TRIGGER_EVENT.byNameCodec().fieldOf("event").forGetter(SkillTrackerDefinition::event),
            SkillTriggerConditionType.INSTANCE_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillTrackerDefinition::conditions),
            SkillTrackerType.CODEC.fieldOf("tracker").forGetter(SkillTrackerDefinition::tracker)
    ).apply(instance, SkillTrackerDefinition::new));

    public List<Component> getInfoComponents() {
        List<Component> infoComponents = new ArrayList<>();
        infoComponents.add(Component.translatable(event.identifier().toLanguageKey("skill.trigger", "info")).withStyle(ChatFormatting.GREEN));
        if (!conditions.isEmpty()) {
            infoComponents.add(Component.literal(" ").append(Component.translatable("tooltip.tarkovcraft_core.skill.conditions")).withStyle(ChatFormatting.GRAY));
            for (SkillTriggerCondition condition : conditions) {
                infoComponents.add(Component.literal(" - ").append(condition.getDescription()).withStyle(ChatFormatting.GRAY));
            }
        }
        return infoComponents;
    }

    public float trigger(Context context) {
        for (SkillTriggerCondition condition : this.conditions) {
            if (!condition.isTriggerable(context))
                return 0.0F;
        }
        if (this.tracker.isTriggerable(context)) {
            return this.tracker.trigger(context);
        }
        return 0.0F;
    }
}
