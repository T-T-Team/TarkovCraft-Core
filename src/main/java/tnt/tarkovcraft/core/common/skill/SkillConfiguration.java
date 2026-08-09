package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.skill.progression.ConstantSkillProgressionStrategy;
import tnt.tarkovcraft.core.common.skill.progression.SkillProgressionStrategy;

public record SkillConfiguration(int maxLevel, SkillProgressionStrategy progressionStrategy, SkillMemoryConfiguration memory) {

    public static final Codec<SkillConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProvider.POSITIVE_INT.optionalFieldOf("max_level", 100).forGetter(SkillConfiguration::maxLevel),
            SkillProgressionStrategy.CODEC.optionalFieldOf("progression_strategy", ConstantSkillProgressionStrategy.DEFAULT).forGetter(SkillConfiguration::progressionStrategy),
            SkillMemoryConfiguration.CODEC.optionalFieldOf("memory", SkillMemoryConfiguration.NO_LOSS).forGetter(SkillConfiguration::memory)
    ).apply(instance, SkillConfiguration::new));
}
