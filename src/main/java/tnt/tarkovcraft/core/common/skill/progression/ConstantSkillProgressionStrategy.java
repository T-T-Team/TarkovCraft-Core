package tnt.tarkovcraft.core.common.skill.progression;

import com.mojang.serialization.MapCodec;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

public record ConstantSkillProgressionStrategy(float experience) implements SkillProgressionStrategy {

    public static final MapCodec<ConstantSkillProgressionStrategy> CODEC = NumberProvider.POSITIVE_FLOAT
            .xmap(ConstantSkillProgressionStrategy::new, ConstantSkillProgressionStrategy::experience)
            .fieldOf("experience");
    public static final ConstantSkillProgressionStrategy DEFAULT = new ConstantSkillProgressionStrategy(20.0F);

    @Override
    public float getRequiredExperience(int skillLevel) {
        return this.experience;
    }

    @Override
    public MapCodec<? extends SkillProgressionStrategy> codec() {
        return CODEC;
    }
}
