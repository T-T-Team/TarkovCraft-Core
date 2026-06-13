package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

public final class SkillMemoryConfiguration {

    public static final Codec<SkillMemoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SkillMemoryConfiguration::isEnabled),
            Codec.BOOL.optionalFieldOf("level_loss", false).forGetter(SkillMemoryConfiguration::canLoseLevel),
            NumberProvider.DURATION.fieldOf("forget_after").forGetter(t -> t.forgetAfter),
            NumberProvider.POSITIVE_FLOAT.fieldOf("forget_amount").forGetter(t -> t.forgetAmount)
    ).apply(instance, SkillMemoryConfiguration::new));
    public static final SkillMemoryConfiguration NO_LOSS = new SkillMemoryConfiguration(false, false, Integer.MAX_VALUE, 0.0F);

    private final boolean canForget;
    private final boolean canLoseLevel;
    private final int forgetAfter;
    private final float forgetAmount;

    public SkillMemoryConfiguration(boolean canForget, boolean canLoseLevel, int forgetAfter, float forgetAmount) {
        this.canForget = canForget;
        this.canLoseLevel = canLoseLevel;
        this.forgetAfter = forgetAfter;
        this.forgetAmount = forgetAmount;
    }

    public boolean isEnabled() {
        return this.canForget;
    }

    public boolean canLoseLevel() {
        return this.canLoseLevel;
    }

    public long getForgetAfter() {
        return Math.max(1L, this.forgetAfter);
    }

    public float getForgetAmount() {
        return Math.max(0.0F, this.forgetAmount);
    }
}
