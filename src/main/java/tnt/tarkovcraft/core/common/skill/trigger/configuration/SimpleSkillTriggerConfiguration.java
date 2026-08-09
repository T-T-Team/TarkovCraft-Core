package tnt.tarkovcraft.core.common.skill.trigger.configuration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.skill.SkillContext;

import java.util.Optional;

public record SimpleSkillTriggerConfiguration(float value, Optional<Float> limit) implements SkillTriggerConfiguration {

    public static final MapCodec<SimpleSkillTriggerConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.POSITIVE_FLOAT.fieldOf("value").forGetter(t -> t.value),
            NumberProvider.POSITIVE_FLOAT.optionalFieldOf("limit").forGetter(t -> t.limit)
    ).apply(instance, SimpleSkillTriggerConfiguration::new));

    @Override
    public boolean canTrigger(SkillContext context) {
        return true;
    }

    @Override
    public float getTriggerExperience(SkillContext context) {
        float limit = this.limit.orElse(Float.MAX_VALUE);
        return Math.min(this.value * context.multiplier(), limit);
    }

    @Override
    public MapCodec<? extends SkillTriggerConfiguration> codec() {
        return CODEC;
    }
}
