package tnt.tarkovcraft.core.common.skill.tracker.condition;

import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public interface SkillTriggerCondition {

    boolean isTriggerable(SkillContext context);

    Component getDescription();

    SkillTriggerConditionType<?> getType();
}
