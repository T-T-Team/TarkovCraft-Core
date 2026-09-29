package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.*;

public record SkillMemoryConfiguration(boolean canLoseLevel, IntProvider startAfter, FloatProvider experienceLoss) {

    public static final Codec<SkillMemoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("level_loss", false).forGetter(SkillMemoryConfiguration::canLoseLevel),
            IntProviders.NON_NEGATIVE_CODEC.fieldOf("start_after").forGetter(t -> t.startAfter),
            FloatProviders.CODEC.fieldOf("experience_loss").forGetter(t -> t.experienceLoss)
    ).apply(instance, SkillMemoryConfiguration::new));
    public static final SkillMemoryConfiguration NO_LOSS = new SkillMemoryConfiguration(false, ConstantInt.ZERO, ConstantFloat.ZERO);

    public boolean isEnabled(RandomSource random) {
        return this.getStartAfter(random) > 0 && this.getExperienceLoss(random) > 0;
    }

    public int getStartAfter(RandomSource random) {
        return this.startAfter.sample(random);
    }

    public float getExperienceLoss(RandomSource random) {
        return this.experienceLoss.sample(random);
    }
}
