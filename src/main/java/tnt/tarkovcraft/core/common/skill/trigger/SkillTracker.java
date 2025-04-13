package tnt.tarkovcraft.core.common.skill.trigger;

import tnt.tarkovcraft.core.util.context.Context;

public interface SkillTracker {

    boolean isTriggerable(Context context);

    float trigger(Context context);

    SkillTrackerType<?> getType();
}
