package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContext;

public record SimpleSkillTracker(NumberProvider value) implements SkillTracker {

    public static final MapCodec<SimpleSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.valueCodec(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, SimpleSkillTracker::new));

    @Override
    public boolean isTriggerable(SkillContext context) {
        return true;
    }

    @Override
    public float trigger(SkillContext context) {
        return this.value.floatValue() * context.multiplier();
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.SIMPLE.get();
    }
}
