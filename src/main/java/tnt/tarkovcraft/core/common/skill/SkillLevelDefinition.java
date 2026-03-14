package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.common.data.number.ConstantNumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.util.Codecs;

public final class SkillLevelDefinition {

    public static final SkillLevelDefinition DEFAULT = new SkillLevelDefinition(ConstantNumberProvider.of(100), ConstantNumberProvider.of(10.0F), ConstantNumberProvider.of(15.0F), ConstantNumberProvider.of((int) Short.MAX_VALUE), ConstantNumberProvider.of(Float.MAX_VALUE));
    public static final Codec<SkillLevelDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NumberProviderType.valueCodec(ExtraCodecs.POSITIVE_INT).optionalFieldOf("max_level", DEFAULT.maxLevel).forGetter(t -> t.maxLevel),
            NumberProviderType.valueCodec(Codecs.NON_NEGATIVE_FLOAT).optionalFieldOf("base_exp", DEFAULT.baseExperience).forGetter(t -> t.baseExperience),
            NumberProviderType.valueCodec(Codecs.NON_NEGATIVE_FLOAT).optionalFieldOf("additional_exp", DEFAULT.additionalExperience).forGetter(t -> t.additionalExperience),
            NumberProviderType.valueCodec(ExtraCodecs.NON_NEGATIVE_INT).optionalFieldOf("max_stack", DEFAULT.maxStack).forGetter(t -> t.maxLevel),
            NumberProviderType.valueCodec(Codecs.NON_NEGATIVE_FLOAT).optionalFieldOf("max_exp", DEFAULT.maxExperience).forGetter(t -> t.maxExperience)
    ).apply(instance, SkillLevelDefinition::new));

    private final NumberProvider maxLevel;
    private final NumberProvider baseExperience;
    private final NumberProvider additionalExperience;
    private final NumberProvider maxStack;
    private final NumberProvider maxExperience;

    public SkillLevelDefinition(NumberProvider maxLevel, NumberProvider baseExperience, NumberProvider additionalExperience, NumberProvider maxStack, NumberProvider maxExperience) {
        this.maxLevel = maxLevel;
        this.baseExperience = baseExperience;
        this.additionalExperience = additionalExperience;
        this.maxStack = maxStack;
        this.maxExperience = maxExperience;
    }

    public int getMaxLevel() {
        return Math.max(0, this.maxLevel.intValue());
    }

    public float getRequiredExperience(int level) {
        int stackMultiplier = Mth.clamp(level, 0, this.maxStack.intValue());
        float baseExperience = Math.max(0.0F, this.baseExperience.floatValue());
        float additionalExperience = Math.max(0.0F, this.additionalExperience.floatValue());
        return Math.min(baseExperience + additionalExperience * stackMultiplier, this.maxExperience.floatValue());
    }
}
