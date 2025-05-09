package tnt.tarkovcraft.core.common.skill.tracker.condition;

import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.context.Context;

public interface SkillTriggerCondition {

    boolean isTriggerable(Context context);

    Component getDescription();

    SkillTriggerConditionType<?> getType();
}
