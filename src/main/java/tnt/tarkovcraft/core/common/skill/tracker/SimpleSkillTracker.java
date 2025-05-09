package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.Context;

public class SimpleSkillTracker implements SkillTracker {

    public static final MapCodec<SimpleSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value))
    ).apply(instance, SimpleSkillTracker::new));

    private final NumberProvider value;

    public SimpleSkillTracker(Either<NumberProvider, Float> value) {
        this.value = NumberProviderType.resolveNoDuration(value);
    }

    @Override
    public boolean isTriggerable(Context context) {
        return true;
    }

    @Override
    public float trigger(Context context) {
        return this.value.floatValue() * context.getOrDefault(SkillContextKeys.SKILL_GAIN_MULTIPLIER, 1.0F);
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.SIMPLE.get();
    }
}
