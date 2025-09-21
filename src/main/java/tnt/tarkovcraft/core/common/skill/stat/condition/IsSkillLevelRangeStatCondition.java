package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.datafixers.util.Either;
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

public class IsSkillLevelRangeStatCondition implements SkillStatCondition {

    public static final MapCodec<IsSkillLevelRangeStatCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_INT).optionalFieldOf("min", Either.left(ConstantNumberProvider.ZERO)).forGetter(t -> Either.left(t.min)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_INT).optionalFieldOf("max", Either.left(ConstantNumberProvider.MAX_INT)).forGetter(t -> Either.left(t.max))
    ).apply(instance, IsSkillLevelRangeStatCondition::new));

    private final NumberProvider min;
    private final NumberProvider max;

    public IsSkillLevelRangeStatCondition(Either<NumberProvider, Integer> min, Either<NumberProvider, Integer> max) {
        this.min = NumberProviderType.resolveNoDuration(min);
        this.max = NumberProviderType.resolveNoDuration(max);
    }

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
