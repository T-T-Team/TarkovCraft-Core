package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.serialization.MapCodec;
import tnt.tarkovcraft.core.common.init.CoreSkillStatConditions;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.Context;

public class IsMaxSkillLevelStatCondition implements SkillStatCondition {

    public static final IsMaxSkillLevelStatCondition INSTANCE = new IsMaxSkillLevelStatCondition();
    public static final MapCodec<IsMaxSkillLevelStatCondition> CODEC = MapCodec.unit(INSTANCE);

    private IsMaxSkillLevelStatCondition() {}

    @Override
    public boolean canApply(Context context) {
        return context.get(SkillContextKeys.SKILL)
                .map(Skill::isMaxLevel)
                .orElse(false);
    }

    @Override
    public SkillStatConditionType<?> getType() {
        return CoreSkillStatConditions.MAX_SKILL_LEVEL.get();
    }
}
