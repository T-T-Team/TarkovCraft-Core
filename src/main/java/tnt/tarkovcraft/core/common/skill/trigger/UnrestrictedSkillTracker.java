package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.data.number.NumberProviderType;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.util.context.Context;

public class UnrestrictedSkillTracker implements SkillTracker {

    public static final MapCodec<UnrestrictedSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value))
    ).apply(instance, UnrestrictedSkillTracker::new));

    private final NumberProvider value;

    public UnrestrictedSkillTracker(Either<NumberProvider, Float> value) {
        this.value = NumberProviderType.resolveNoDuration(value);
    }

    @Override
    public boolean isTriggerable(Context context) {
        return true;
    }

    @Override
    public float trigger(Context context) {
        return this.value.map(Double::floatValue) * context.getOrDefault(SkillContextKeys.SKILL_GAIN_MULTIPLIER, 1.0F);
    }

    @Override
    public Tag getSaveData() {
        return null;
    }

    @Override
    public void loadSaveData(Tag tag) {
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.UNRESTRICTED.get();
    }
}
