package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public record SkillTrackerDefinition(SkillTriggerEvent event, SkillTracker tracker) {

    public static final Codec<SkillTrackerDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TarkovCraftRegistries.SKILL_TRIGGER_EVENT.byNameCodec().fieldOf("event").forGetter(SkillTrackerDefinition::event),
            SkillTrackerType.INSTANCE_CODEC.fieldOf("tracker").forGetter(SkillTrackerDefinition::tracker)
    ).apply(instance, SkillTrackerDefinition::new));
}
