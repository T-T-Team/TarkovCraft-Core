package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.common.data.Duration;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.ContextKeys;
import tnt.tarkovcraft.core.util.context.Context;

public class LimitedSkillTracker implements SkillTracker {

    public static final MapCodec<LimitedSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("limit").forGetter(t -> Either.left(t.maxValue)),
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_INT).fieldOf("cooldown").forGetter(t -> Either.left(t.cooldown)),
            Codec.FLOAT.optionalFieldOf("accumulated", 0.0F).forGetter(t -> t.accumulatedValue),
            Codec.LONG.optionalFieldOf("cooldownUntil", 0L).forGetter(t -> t.cooldownUntil)
    ).apply(instance, LimitedSkillTracker::new));

    private final NumberProvider value;
    private final NumberProvider maxValue;
    private final NumberProvider cooldown;
    private float accumulatedValue;
    private long cooldownUntil;

    private LimitedSkillTracker(Either<NumberProvider, Float> value, Either<NumberProvider, Float> maxValue, Either<NumberProvider, Either<Duration, Integer>> cooldown, float accumulatedValue, long cooldownUntil) {
        this.value = NumberProviderType.resolveNoDuration(value);
        this.maxValue = NumberProviderType.resolveNoDuration(maxValue);
        this.cooldown = NumberProviderType.resolve(cooldown);
        this.accumulatedValue = accumulatedValue;
        this.cooldownUntil = cooldownUntil;
    }

    @Override
    public boolean isTriggerable(Context context) {
        Level level = context.getOrThrow(ContextKeys.LEVEL);
        long currentTime = level.getGameTime();
        return this.cooldownUntil < currentTime;
    }

    @Override
    public float trigger(Context context) {
        float limit = this.maxValue.map(Double::floatValue);
        float multiplier = context.getOrDefault(SkillContextKeys.SKILL_GAIN_MULTIPLIER, 1.0F);
        float fullTriggerAmount = this.value.map(Double::floatValue) * multiplier;
        float triggerAmount = Math.max(fullTriggerAmount, limit - this.accumulatedValue);
        this.accumulatedValue += triggerAmount;
        if (this.accumulatedValue >= limit) {
            this.accumulatedValue = 0.0F;
            int cooldown = Mth.ceil(this.cooldown.getNumber());
            Level level = context.getOrThrow(ContextKeys.LEVEL);
            this.cooldownUntil = level.getGameTime() + cooldown;
        }
        return triggerAmount;
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.LIMITED.get();
    }
}
