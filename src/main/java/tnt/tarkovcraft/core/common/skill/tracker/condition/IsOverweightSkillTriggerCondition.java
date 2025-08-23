package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.common.weight.WeightSystem;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextKeys;

public class IsOverweightSkillTriggerCondition implements SkillTriggerCondition {

    private static final Component DESCRIPTION = Component.translatable("skill.condition.overweight");
    private static final IsOverweightSkillTriggerCondition INSTANCE = new IsOverweightSkillTriggerCondition();
    public static final MapCodec<IsOverweightSkillTriggerCondition> CODEC = MapCodec.unit(INSTANCE);

    private IsOverweightSkillTriggerCondition() {
    }

    @Override
    public boolean isTriggerable(Context context) {
        Entity entity = context.getOrThrow(ContextKeys.ENTITY);
        return entity instanceof LivingEntity livingEntity && WeightSystem.isOverweight(livingEntity);
    }

    @Override
    public Component getDescription() {
        return DESCRIPTION;
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.IS_OVERWEIGHT.get();
    }
}
