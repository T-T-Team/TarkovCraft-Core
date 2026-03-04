package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.data.number.ConstantNumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillStatConditions;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

public record IsSkillLevelRangeStatCondition(NumberProvider min, NumberProvider max) implements SkillStatCondition {

    public static final MapCodec<IsSkillLevelRangeStatCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.valueCodec(ExtraCodecs.POSITIVE_INT).optionalFieldOf("min", ConstantNumberProvider.ZERO).forGetter(t -> t.min),
            NumberProviderType.valueCodec(ExtraCodecs.POSITIVE_INT).optionalFieldOf("max", ConstantNumberProvider.MAX_INT).forGetter(t -> t.max)
    ).apply(instance, IsSkillLevelRangeStatCondition::new));

    @Override
    public boolean canApply(SkillDefinition definition, Skill skill, Entity entity) {
        int level = skill.getLevel();
        int minValue = this.min.intValue();
        int maxValue = this.max.intValue();
        int from = Math.min(minValue, maxValue);
        int to = Math.max(maxValue, minValue);
        return level >= from && level <= to;
    }

    @Override
    public SkillStatConditionType<?> getType() {
        return CoreSkillStatConditions.SKILL_LEVEL_RANGE.get();
    }
}
