package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.number.ConstantNumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillStatConditions;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.Context;

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
    public boolean canApply(Context context) {
        return context.get(SkillContextKeys.SKILL).map(skill -> {
            int skillLevel = skill.getLevel();
            int from = this.min.intValue();
            int to = this.max.intValue();
            return skillLevel >= from && skillLevel <= to;
        }).orElse(false);
    }

    @Override
    public SkillStatConditionType<?> getType() {
        return CoreSkillStatConditions.SKILL_LEVEL_RANGE.get();
    }
}
