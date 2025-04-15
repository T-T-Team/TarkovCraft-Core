package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

import java.util.Objects;

public record SkillStatConditionType<S extends SkillStatCondition>(ResourceLocation identifier, MapCodec<S> codec) {

    public static final Codec<SkillStatCondition> INSTANCE_CODEC = TarkovCraftRegistries.SKILL_STAT_CONDITION_TYPE.byNameCodec().dispatch(SkillStatCondition::getType, SkillStatConditionType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SkillStatConditionType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
