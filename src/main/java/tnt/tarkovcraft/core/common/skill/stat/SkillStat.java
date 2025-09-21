package tnt.tarkovcraft.core.common.skill.stat;

import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

public interface SkillStat {

    void apply(SkillDefinition definition, Skill skill, Entity entity);

    void clear(SkillDefinition definition, Skill skill, Entity entity);

    Object[] getTranslationData(SkillDefinition definition, Skill skill, Entity entity);

    SkillStatType<?> getType();
}
