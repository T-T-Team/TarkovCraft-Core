package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreSkillStatConditions;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

public final class IsMaxSkillLevelStatCondition implements SkillStatCondition {

    public static final IsMaxSkillLevelStatCondition INSTANCE = new IsMaxSkillLevelStatCondition();
    public static final MapCodec<IsMaxSkillLevelStatCondition> CODEC = MapCodec.unit(INSTANCE);

    private IsMaxSkillLevelStatCondition() {}

    @Override
    public boolean canApply(SkillDefinition definition, Skill skill, Entity entity) {
        return skill.isMaxLevel();
    }

    @Override
    public SkillStatConditionType<?> getType() {
        return CoreSkillStatConditions.MAX_SKILL_LEVEL.get();
    }
}
