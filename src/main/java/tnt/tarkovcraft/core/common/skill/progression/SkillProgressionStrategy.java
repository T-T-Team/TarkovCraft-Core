package tnt.tarkovcraft.core.common.skill.progression;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.function.Function;

public interface SkillProgressionStrategy {

    Codec<SkillProgressionStrategy> CODEC = CoreRegistries.SKILL_PROGRESSION_STRATEGY.byNameCodec().dispatch(SkillProgressionStrategy::codec, Function.identity());

    float getRequiredExperience(int skillLevel);

    MapCodec<? extends SkillProgressionStrategy> codec();
}
