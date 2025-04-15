package tnt.tarkovcraft.core.common.skill.stat.condition;

import tnt.tarkovcraft.core.util.context.Context;

public interface SkillStatCondition {

    boolean canApply(Context context);

    SkillStatConditionType<?> getType();
}
