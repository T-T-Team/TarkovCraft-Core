package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.common.data.duration.Duration;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextKeys;

public class LimitedSkillTrackerConfiguration implements SkillTrackerConfiguration<LimitedSkillTrackerConfiguration.Tracker> {

    public static final MapCodec<LimitedSkillTrackerConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("limit").forGetter(t -> Either.left(t.maxValue)),
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_INT).fieldOf("cooldown").forGetter(t -> Either.left(t.cooldown))
    ).apply(instance, LimitedSkillTrackerConfiguration::new));

    private final NumberProvider value;
    private final NumberProvider maxValue;
    private final NumberProvider cooldown;

    private LimitedSkillTrackerConfiguration(Either<NumberProvider, Float> value, Either<NumberProvider, Float> maxValue, Either<NumberProvider, Either<Duration, Integer>> cooldown) {
        this.value = NumberProviderType.resolveNoDuration(value);
        this.maxValue = NumberProviderType.resolveNoDuration(maxValue);
        this.cooldown = NumberProviderType.resolve(cooldown);
    }

    @Override
    public boolean isTriggerable(Context context, Tracker tracker) {
        Level level = context.getOrThrow(ContextKeys.LEVEL);
        long currentTime = level.getGameTime();
        return tracker.getCooldownUntil() < currentTime;
    }

    @Override
    public float trigger(Context context, Tracker tracker) {
        Level level = context.getOrThrow(ContextKeys.LEVEL);
        long lastAccumulatedTime = tracker.getLastAccumulated();
        long gameTime = level.getGameTime();
        long cooldown = this.cooldown.map(Double::longValue);
        // Reset accumulated value after cooldown
        if (lastAccumulatedTime - gameTime >= cooldown) {
            tracker.setAccumulated(0.0F);
        }

        float limit = this.maxValue.floatValue();
        float multiplier = context.getOrDefault(SkillContextKeys.SKILL_GAIN_MULTIPLIER, 1.0F);
        float fullTriggerAmount = this.value.floatValue() * multiplier;
        float triggerAmount = Math.min(fullTriggerAmount, limit - tracker.getAccumulated());
        tracker.accumulated += triggerAmount;
        if (tracker.getAccumulated() >= limit) {
            tracker.setAccumulated(0.0F);
            tracker.setCooldownUntil(level.getGameTime() + cooldown);
        }
        tracker.setLastAccumulated(gameTime);
        return triggerAmount;
    }

    @Override
    public Tracker createDataTracker() {
        return new Tracker(0.0F, 0L, 0L);
    }

    @Override
    public SkillTrackerType<Tracker, ?> getType() {
        return CoreSkillTrackers.LIMITED.get();
    }

    public static final class Tracker implements SkillTracker {

        public static final MapCodec<Tracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.FLOAT.fieldOf("accumulated").forGetter(Tracker::getAccumulated),
                Codec.LONG.fieldOf("cooldown").forGetter(Tracker::getCooldownUntil),
                Codec.LONG.fieldOf("lastAccumulated").forGetter(Tracker::getLastAccumulated)
        ).apply(instance, Tracker::new));

        private float accumulated;
        private long cooldownUntil;
        private long lastAccumulated;

        private Tracker(float accumulated, long cooldownUntil, long lastAccumulated) {
            this.accumulated = accumulated;
            this.cooldownUntil = cooldownUntil;
            this.lastAccumulated = lastAccumulated;
        }

        public float getAccumulated() {
            return accumulated;
        }

        public void setAccumulated(float accumulated) {
            this.accumulated = accumulated;
        }

        public long getCooldownUntil() {
            return cooldownUntil;
        }

        public void setCooldownUntil(long cooldownUntil) {
            this.cooldownUntil = cooldownUntil;
        }

        public long getLastAccumulated() {
            return lastAccumulated;
        }

        public void setLastAccumulated(long lastAccumulated) {
            this.lastAccumulated = lastAccumulated;
        }

        @Override
        public SkillTrackerType<?, ?> getType() {
            return CoreSkillTrackers.LIMITED.get();
        }
    }
}
