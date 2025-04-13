package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.tracker.condition.SkillTriggerCondition;
import tnt.tarkovcraft.core.common.skill.tracker.condition.SkillTriggerConditionType;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record SkillTrackerDefinition(UUID id, SkillTriggerEvent event, List<SkillTriggerCondition> conditions, SkillTrackerConfiguration<?> tracker) {

    public static final Codec<SkillTrackerDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(t -> t.id),
            TarkovCraftRegistries.SKILL_TRIGGER_EVENT.byNameCodec().fieldOf("event").forGetter(SkillTrackerDefinition::event),
            SkillTriggerConditionType.INSTANCE_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillTrackerDefinition::conditions),
            SkillTrackerType.CONFIGURATION_CODEC.fieldOf("tracker").forGetter(SkillTrackerDefinition::tracker)
    ).apply(instance, SkillTrackerDefinition::new));

    public SkillTracker createTracker() {
        return this.tracker.createDataTracker();
    }

    @SuppressWarnings("unchecked")
    public <T extends SkillTracker> float trigger(Context context, T tracker) {
        for (SkillTriggerCondition condition : this.conditions) {
            if (!condition.isTriggerable(context))
                return 0.0F;
        }
        SkillTrackerConfiguration<T> config = (SkillTrackerConfiguration<T>) this.tracker;
        if (config.isTriggerable(context, tracker)) {
            return config.trigger(context, tracker);
        }
        return 0.0F;
    }
}
