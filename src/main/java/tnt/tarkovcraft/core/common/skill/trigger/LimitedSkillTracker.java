package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import tnt.tarkovcraft.core.common.data.Duration;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextKeys;

public class LimitedSkillTracker implements SkillTracker {

    public static final MapCodec<LimitedSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value)),
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("limit").forGetter(t -> Either.left(t.maxValue)),
            NumberProviderType.complexCodec(ExtraCodecs.POSITIVE_INT).fieldOf("cooldown").forGetter(t -> Either.left(t.cooldown))
    ).apply(instance, LimitedSkillTracker::new));

    private final NumberProvider value;
    private final NumberProvider maxValue;
    private final NumberProvider cooldown;
    private float accumulatedValue;
    private long cooldownUntil;

    private LimitedSkillTracker(Either<NumberProvider, Float> value, Either<NumberProvider, Float> maxValue, Either<NumberProvider, Either<Duration, Integer>> cooldown) {
        this.value = NumberProviderType.resolveNoDuration(value);
        this.maxValue = NumberProviderType.resolveNoDuration(maxValue);
        this.cooldown = NumberProviderType.resolve(cooldown);
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
        float triggerAmount = Math.min(fullTriggerAmount, limit - this.accumulatedValue);
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
    public Tag getSaveData() {
        return Codecs.serialize(NbtOps.INSTANCE, SaveData.CODEC, new SaveData(this.accumulatedValue, this.cooldownUntil));
    }

    @Override
    public void loadSaveData(Tag tag) {
        SaveData data = Codecs.deserialize(NbtOps.INSTANCE, SaveData.CODEC, tag);
        this.accumulatedValue = data.accumulated;
        this.cooldownUntil = data.cooldownUntil;
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.LIMITED.get();
    }

    private record SaveData(float accumulated, long cooldownUntil) {

        private static final Codec<SaveData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("accumulated").forGetter(SaveData::accumulated),
                Codec.LONG.fieldOf("cooldownUntil").forGetter(SaveData::cooldownUntil)
        ).apply(instance, SaveData::new));
    }
}
