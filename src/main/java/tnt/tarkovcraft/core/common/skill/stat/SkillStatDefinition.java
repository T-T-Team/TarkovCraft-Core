package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.skill.stat.condition.SkillStatCondition;
import tnt.tarkovcraft.core.common.skill.stat.condition.SkillStatConditionType;

import java.util.Collections;
import java.util.List;

public record SkillStatDefinition(List<SkillStatCondition> conditions, SkillStatDisplayInformation display, SkillStat stat) {

    public static final Codec<SkillStatDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillStatConditionType.INSTANCE_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillStatDefinition::conditions),
            SkillStatDisplayInformation.CODEC.fieldOf("display").forGetter(SkillStatDefinition::display),
            SkillStatType.INSTANCE_CODEC.fieldOf("apply").forGetter(SkillStatDefinition::stat)
    ).apply(instance, SkillStatDefinition::new));

    public boolean isAvailable(SkillDefinition definition, Skill skill, Entity entity) {
        return this.conditions.stream().allMatch(c -> c.canApply(definition, skill, entity));
    }
}
