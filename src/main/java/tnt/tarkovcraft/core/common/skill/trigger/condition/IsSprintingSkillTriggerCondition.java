package tnt.tarkovcraft.core.common.skill.trigger.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public record IsSprintingSkillTriggerCondition(boolean invert) implements SkillTriggerCondition {

    public static final MapCodec<IsSprintingSkillTriggerCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("invert", false).forGetter(t -> t.invert)
    ).apply(instance, IsSprintingSkillTriggerCondition::new));

    @Override
    public boolean isTriggerable(SkillContext context) {
        Entity entity = context.entity();
        boolean sprinting = entity.isSprinting();
        return this.invert != sprinting;
    }

    @Override
    public Component getDescription() {
        return invert ? Component.translatable("skill.condition.not_sprinting") : Component.translatable("skill.condition.sprinting");
    }

    @Override
    public MapCodec<? extends SkillTriggerCondition> codec() {
        return CODEC;
    }
}
