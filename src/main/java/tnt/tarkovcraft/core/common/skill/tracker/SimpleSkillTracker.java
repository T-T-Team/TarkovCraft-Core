package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.skill.SkillContext;

import java.util.Optional;

public record SimpleSkillTracker(float value, Optional<Float> limit) implements SkillTracker {

    public static final MapCodec<SimpleSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.POSITIVE_FLOAT.fieldOf("value").forGetter(t -> t.value),
            NumberProvider.POSITIVE_FLOAT.optionalFieldOf("limit").forGetter(t -> t.limit)
    ).apply(instance, SimpleSkillTracker::new));

    @Override
    public boolean isTriggerable(SkillContext context) {
        return true;
    }

    @Override
    public float trigger(SkillContext context) {
        float limit = this.limit.orElse(Float.MAX_VALUE);
        return Math.min(this.value * context.multiplier(), limit);
    }

    @Override
    public MapCodec<? extends SkillTracker> codec() {
        return CODEC;
    }
}
