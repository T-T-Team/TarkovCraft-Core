package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

public final class SkillLevelDefinition {

    public static final SkillLevelDefinition DEFAULT = new SkillLevelDefinition(100, 10.0F, 15.0F, Short.MAX_VALUE, Float.MAX_VALUE);
    public static final Codec<SkillLevelDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProvider.POSITIVE_INT.optionalFieldOf("max_level", DEFAULT.maxLevel).forGetter(t -> t.maxLevel),
            NumberProvider.NON_NEGATIVE_FLOAT.optionalFieldOf("base_exp", DEFAULT.baseExperience).forGetter(t -> t.baseExperience),
            NumberProvider.NON_NEGATIVE_FLOAT.optionalFieldOf("additional_exp", DEFAULT.additionalExperience).forGetter(t -> t.additionalExperience),
            NumberProvider.NON_NEGATIVE_INT.optionalFieldOf("max_stack", DEFAULT.maxStack).forGetter(t -> t.maxLevel),
            NumberProvider.NON_NEGATIVE_FLOAT.optionalFieldOf("max_exp", DEFAULT.maxExperience).forGetter(t -> t.maxExperience)
    ).apply(instance, SkillLevelDefinition::new));

    private final int maxLevel;
    private final float baseExperience;
    private final float additionalExperience;
    private final int maxStack;
    private final float maxExperience;

    public SkillLevelDefinition(int maxLevel, float baseExperience, float additionalExperience, int maxStack, float maxExperience) {
        this.maxLevel = maxLevel;
        this.baseExperience = baseExperience;
        this.additionalExperience = additionalExperience;
        this.maxStack = maxStack;
        this.maxExperience = maxExperience;
    }

    public int getMaxLevel() {
        return Math.max(0, this.maxLevel);
    }

    public float getRequiredExperience(int level) {
        int stackMultiplier = Mth.clamp(level, 0, this.maxStack);
        float baseExperience = Math.max(0.0F, this.baseExperience);
        float additionalExperience = Math.max(0.0F, this.additionalExperience);
        return Math.min(baseExperience + additionalExperience * stackMultiplier, this.maxExperience);
    }
}
