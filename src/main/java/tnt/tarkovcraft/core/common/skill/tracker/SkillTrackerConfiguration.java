package tnt.tarkovcraft.core.common.skill.tracker;

import tnt.tarkovcraft.core.util.context.Context;

public interface SkillTrackerConfiguration<T extends SkillTracker> {

    boolean isTriggerable(Context context, T tracker);

    float trigger(Context context, T tracker);

    T createDataTracker();

    SkillTrackerType<T, ?> getType();
}
