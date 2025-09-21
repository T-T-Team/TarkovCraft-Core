package tnt.tarkovcraft.core.common.skill.tracker;

import tnt.tarkovcraft.core.common.skill.SkillContext;

public interface SkillTracker {

    boolean isTriggerable(SkillContext context);

    float trigger(SkillContext context);

    SkillTrackerType<?> getType();
}
