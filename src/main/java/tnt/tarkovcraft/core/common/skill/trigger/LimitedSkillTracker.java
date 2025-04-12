package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.common.data.Duration;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;

public class LimitedSkillTracker implements SkillTracker {

    public static final MapCodec<LimitedSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value)),
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_FLOAT).fieldOf("limit").forGetter(t -> Either.left(t.maxValue)),
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_INT).fieldOf("cooldown").forGetter(t -> Either.left(t.cooldown)),
            Codec.FLOAT.optionalFieldOf("accumulated", 0.0F).forGetter(t -> t.accumulatedValue),
            Codec.LONG.optionalFieldOf("cooldownUntil", 0L).forGetter(t -> t.cooldownUntil)
    ).apply(instance, LimitedSkillTracker::new));

    private final NumberProvider value;
    private final NumberProvider maxValue;
    private final NumberProvider cooldown;
    private float accumulatedValue;
    private long cooldownUntil;

    private LimitedSkillTracker(Either<NumberProvider, Either<Duration, Float>> value, Either<NumberProvider, Either<Duration, Float>> maxValue, Either<NumberProvider, Either<Duration, Integer>> cooldown, float accumulatedValue, long cooldownUntil) {
        this.value = NumberProviderType.resolve(value);
        this.maxValue = NumberProviderType.resolve(maxValue);
        this.cooldown = NumberProviderType.resolve(cooldown);
        this.accumulatedValue = accumulatedValue;
        this.cooldownUntil = cooldownUntil;
    }

    @Override
    public boolean isTriggerable(SkillTriggerEvent event, Entity entity) {
        long currentTime = entity.level().getGameTime();
        return this.cooldownUntil < currentTime;
    }

    @Override
    public float trigger(SkillTriggerEvent event, Entity entity) {
        float limit = (float) this.maxValue.getNumber();
        float fullTriggerAmount = (float) this.value.getNumber();
        float triggerAmount = Math.max(fullTriggerAmount, limit - this.accumulatedValue);
        this.accumulatedValue += triggerAmount;
        Level level = entity.level();
        if (this.accumulatedValue >= limit) {
            this.accumulatedValue = 0.0F;
            int cooldown = Mth.ceil(this.cooldown.getNumber());
            this.cooldownUntil = level.getGameTime() + cooldown;
        }
        return triggerAmount;
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.LIMITED.get();
    }
}
