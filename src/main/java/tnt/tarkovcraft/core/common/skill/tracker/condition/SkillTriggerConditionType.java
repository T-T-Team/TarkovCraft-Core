package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;

public record SkillTriggerConditionType<C extends SkillTriggerCondition>(ResourceLocation identifier, MapCodec<C> codec) {

    public static final Codec<SkillTriggerCondition> INSTANCE_CODEC = CoreRegistries.SKILL_TRIGGER_CONDITION_TYPE.byNameCodec().dispatch(SkillTriggerCondition::getType, SkillTriggerConditionType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SkillTriggerConditionType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
