package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public record SimpleSkillTracker(float value) implements SkillTracker {

    public static final MapCodec<SimpleSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.POSITIVE_FLOAT.fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, SimpleSkillTracker::new));

    @Override
    public boolean isTriggerable(SkillContext context) {
        return true;
    }

    @Override
    public float trigger(SkillContext context) {
        return this.value * context.multiplier();
    }

    @Override
    public MapCodec<? extends SkillTracker> codec() {
        return CODEC;
    }
}
