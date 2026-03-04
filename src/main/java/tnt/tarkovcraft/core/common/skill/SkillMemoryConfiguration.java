package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.number.ConstantNumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;

public final class SkillMemoryConfiguration {

    public static final Codec<SkillMemoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SkillMemoryConfiguration::isEnabled),
            Codec.BOOL.optionalFieldOf("level_loss", false).forGetter(SkillMemoryConfiguration::canLoseLevel),
            NumberProviderType.durationCodec(ExtraCodecs.POSITIVE_INT).fieldOf("forget_after").forGetter(t -> t.forgetAfter),
            NumberProviderType.valueCodec(ExtraCodecs.POSITIVE_FLOAT).fieldOf("forget_amount").forGetter(t -> t.forgetAmount)
    ).apply(instance, SkillMemoryConfiguration::new));
    public static final SkillMemoryConfiguration NO_LOSS = new SkillMemoryConfiguration(false, false, ConstantNumberProvider.MAX_INT, ConstantNumberProvider.ZERO);

    private final boolean canForget;
    private final boolean canLoseLevel;
    private final NumberProvider forgetAfter;
    private final NumberProvider forgetAmount;

    public SkillMemoryConfiguration(boolean canForget, boolean canLoseLevel, NumberProvider forgetAfter, NumberProvider forgetAmount) {
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
        return Math.max(1L, this.forgetAfter.map(Double::longValue));
    }

    public float getForgetAmount() {
        return Math.max(0.0F, this.forgetAmount.floatValue());
    }
}
