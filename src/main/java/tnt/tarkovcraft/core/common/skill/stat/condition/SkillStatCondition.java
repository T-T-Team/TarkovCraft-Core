package tnt.tarkovcraft.core.common.skill.stat.condition;

import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

public interface SkillStatCondition {

    boolean canApply(SkillDefinition definition, Skill skill, Entity entity);

    SkillStatConditionType<?> getType();
}
