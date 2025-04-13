package tnt.tarkovcraft.core.common.skill.tracker.condition;

import tnt.tarkovcraft.core.util.context.Context;

public interface SkillTriggerCondition {

    boolean isTriggerable(Context context);

    SkillTriggerConditionType<?> getType();
}
