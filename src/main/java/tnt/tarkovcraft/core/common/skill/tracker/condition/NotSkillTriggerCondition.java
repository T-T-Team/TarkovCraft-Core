package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public record NotSkillTriggerCondition(SkillTriggerCondition child) implements SkillTriggerCondition {

    public static final MapCodec<NotSkillTriggerCondition> CODEC = SkillTriggerConditionType.INSTANCE_CODEC
            .xmap(NotSkillTriggerCondition::new, NotSkillTriggerCondition::child).fieldOf("value");

    @Override
    public boolean isTriggerable(SkillContext context) {
        return this.child.isTriggerable(context);
    }

    @Override
    public Component getDescription() {
        return Component.translatable("skill.condition.not", this.child.getDescription());
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.NOT.get();
    }
}
