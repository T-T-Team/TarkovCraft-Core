package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.trigger.condition.SkillTriggerCondition;
import tnt.tarkovcraft.core.common.skill.trigger.condition.SkillTriggerConditionType;

import java.util.Collections;
import java.util.List;

public record SkillTrackerDefinition(SkillTriggerEvent event, List<SkillTriggerCondition> conditions, SkillTracker tracker) {

    public static final Codec<SkillTrackerDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TarkovCraftRegistries.SKILL_TRIGGER_EVENT.byNameCodec().fieldOf("event").forGetter(SkillTrackerDefinition::event),
            SkillTriggerConditionType.INSTANCE_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillTrackerDefinition::conditions),
            SkillTrackerType.INSTANCE_CODEC.fieldOf("tracker").forGetter(SkillTrackerDefinition::tracker)
    ).apply(instance, SkillTrackerDefinition::new));
}
