package tnt.tarkovcraft.core.common.skill.tracker;

import tnt.tarkovcraft.core.util.context.Context;

public interface SkillTracker {

    boolean isTriggerable(Context context);

    float trigger(Context context);

    default SkillTrackerType<?> getType() {
        return null;
    }
}
