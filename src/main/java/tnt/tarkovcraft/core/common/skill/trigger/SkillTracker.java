package tnt.tarkovcraft.core.common.skill.trigger;

import net.minecraft.world.entity.Entity;

public interface SkillTracker {

    boolean isTriggerable(SkillTriggerEvent event, Entity entity);

    float trigger(SkillTriggerEvent event, Entity entity);

    SkillTrackerType<?> getType();
}
