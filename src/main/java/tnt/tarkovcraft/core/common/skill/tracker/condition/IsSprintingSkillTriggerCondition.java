package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerConditions;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextKeys;

public class IsSprintingSkillTriggerCondition implements SkillTriggerCondition {

    public static final MapCodec<IsSprintingSkillTriggerCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("invert", false).forGetter(t -> t.invert)
    ).apply(instance, IsSprintingSkillTriggerCondition::new));
    private final boolean invert;

    public IsSprintingSkillTriggerCondition(boolean invert) {
        this.invert = invert;
    }

    @Override
    public boolean isTriggerable(Context context) {
        Entity entity = context.getOrThrow(ContextKeys.ENTITY);
        boolean sprinting = entity.isSprinting();
        return this.invert != sprinting;
    }

    @Override
    public Component getDescription() {
        return invert ? Component.translatable("skill.condition.not_sprinting") : Component.translatable("skill.condition.sprinting");
    }

    @Override
    public SkillTriggerConditionType<?> getType() {
        return CoreSkillTriggerConditions.IS_SPRINTING.get();
    }
}
