package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;

public record ExternalSkillData(boolean enabled, int maxLevel, int baseExperience, float levelingFactor,
                                boolean canLoseExperience, boolean canLoseLevel) {

    public static final Codec<ExternalSkillData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(ExternalSkillData::enabled),
            ExtraCodecs.intRange(1, Integer.MAX_VALUE).optionalFieldOf("maxLevel", 100).forGetter(ExternalSkillData::maxLevel),
            ExtraCodecs.intRange(1, 100_000).optionalFieldOf("experience", 100).forGetter(ExternalSkillData::baseExperience),
            ExtraCodecs.floatRange(1.0F, 100.0F).optionalFieldOf("levelFactor", 1.25F).forGetter(ExternalSkillData::levelingFactor),
            Codec.BOOL.optionalFieldOf("canLoseExperience", true).forGetter(ExternalSkillData::canLoseExperience),
            Codec.BOOL.optionalFieldOf("canLoseLevel", true).forGetter(ExternalSkillData::canLoseLevel)
    ).apply(instance, ExternalSkillData::new));
}
