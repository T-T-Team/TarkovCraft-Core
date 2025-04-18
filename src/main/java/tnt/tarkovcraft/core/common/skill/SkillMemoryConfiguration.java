package tnt.tarkovcraft.core.common.skill;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.Duration;
import tnt.tarkovcraft.core.common.data.number.ConstantNumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;

public final class SkillMemoryConfiguration {

    public static final Codec<SkillMemoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SkillMemoryConfiguration::isEnabled),
            Codec.BOOL.optionalFieldOf("canLoseLevel", false).forGetter(SkillMemoryConfiguration::canLoseLevel),
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_INT).fieldOf("forgetAfter").forGetter(t -> Either.left(t.forgetAfter)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("forgetAmount").forGetter(t -> Either.left(t.forgetAmount))
    ).apply(instance, SkillMemoryConfiguration::new));
    public static final SkillMemoryConfiguration NO_LOSS = new SkillMemoryConfiguration(false, false, Either.left(ConstantNumberProvider.MAX_INT), Either.left(ConstantNumberProvider.ZERO));

    private final boolean canForget;
    private final boolean canLoseLevel;
    private final NumberProvider forgetAfter;
    private final NumberProvider forgetAmount;

    public SkillMemoryConfiguration(boolean canForget, boolean canLoseLevel, Either<NumberProvider, Either<Duration, Integer>> forgetAfter, Either<NumberProvider, Float> forgetAmount) {
        this.canForget = canForget;
        this.canLoseLevel = canLoseLevel;
        this.forgetAfter = NumberProviderType.resolve(forgetAfter);
        this.forgetAmount = NumberProviderType.resolveNoDuration(forgetAmount);
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
