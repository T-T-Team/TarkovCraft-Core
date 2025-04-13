package tnt.tarkovcraft.core.common.skill;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;

public final class SkillLevelDefinition {

    public static final SkillLevelDefinition DEFAULT = new SkillLevelDefinition(Either.right(100), Either.right(10.0F), Either.right(15.0F), Either.right((int) Short.MAX_VALUE));
    public static final Codec<SkillLevelDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_INT).optionalFieldOf("maxLevel", Either.left(DEFAULT.maxLevel)).forGetter(t -> Either.left(t.maxLevel)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).optionalFieldOf("baseExperience", Either.left(DEFAULT.baseExperience)).forGetter(t -> Either.left(t.baseExperience)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).optionalFieldOf("additionalExperience", Either.left(DEFAULT.additionalExperience)).forGetter(t -> Either.left(t.additionalExperience)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_INT).optionalFieldOf("maxStack", Either.left(DEFAULT.maxStack)).forGetter(t -> Either.left(t.maxLevel))
    ).apply(instance, SkillLevelDefinition::new));

    private final NumberProvider maxLevel;
    private final NumberProvider baseExperience;
    private final NumberProvider additionalExperience;
    private final NumberProvider maxStack;

    public SkillLevelDefinition(Either<NumberProvider, Integer> maxLevel, Either<NumberProvider, Float> baseExperience, Either<NumberProvider, Float> additionalExperience, Either<NumberProvider, Integer> maxStack) {
        this.maxLevel = NumberProviderType.resolveNoDuration(maxLevel);
        this.baseExperience = NumberProviderType.resolveNoDuration(baseExperience);
        this.additionalExperience = NumberProviderType.resolveNoDuration(additionalExperience);
        this.maxStack = NumberProviderType.resolveNoDuration(maxStack);
    }

    public int getMaxLevel() {
        return Math.max(0, this.maxLevel.intValue());
    }

    public float getRequiredExperience(int level) {
        int stackMultiplier = Mth.clamp(level, 0, this.maxStack.intValue());
        float baseExperience = Math.max(0.0F, this.baseExperience.floatValue());
        float additionalExperience = Math.max(0.0F, this.additionalExperience.floatValue());
        return baseExperience + additionalExperience * stackMultiplier;
    }
}
