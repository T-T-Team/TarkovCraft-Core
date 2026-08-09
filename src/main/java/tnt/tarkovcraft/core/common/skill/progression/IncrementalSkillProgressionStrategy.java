package tnt.tarkovcraft.core.common.skill.progression;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

import java.util.Optional;

public record IncrementalSkillProgressionStrategy(float initialExperience, float perLevelExperience, Optional<Float> maxExperience) implements SkillProgressionStrategy {

    public static final MapCodec<IncrementalSkillProgressionStrategy> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.POSITIVE_FLOAT.fieldOf("initial_experience").forGetter(IncrementalSkillProgressionStrategy::initialExperience),
            NumberProvider.POSITIVE_FLOAT.fieldOf("per_level_experience").forGetter(IncrementalSkillProgressionStrategy::perLevelExperience),
            NumberProvider.POSITIVE_FLOAT.optionalFieldOf("max_experience").forGetter(IncrementalSkillProgressionStrategy::maxExperience)
    ).apply(instance, IncrementalSkillProgressionStrategy::new));

    @Override
    public float getRequiredExperience(int skillLevel) {
        float addedRequirement = skillLevel * this.perLevelExperience;
        return this.initialExperience + addedRequirement;
    }

    @Override
    public MapCodec<? extends SkillProgressionStrategy> codec() {
        return CODEC;
    }
}
