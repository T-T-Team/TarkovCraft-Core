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

public class UnrestrictedSkillTrackerConfigurationConfiguration implements SkillTrackerConfiguration<UnrestrictedSkillTrackerConfigurationConfiguration.UnrestrictedDataHolder> {

    public static final MapCodec<UnrestrictedSkillTrackerConfigurationConfiguration> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviderType.complexCodecNoDuration(ExtraCodecs.POSITIVE_FLOAT).fieldOf("value").forGetter(t -> Either.left(t.value))
    ).apply(instance, UnrestrictedSkillTrackerConfigurationConfiguration::new));

    private final NumberProvider value;

    public UnrestrictedSkillTrackerConfigurationConfiguration(Either<NumberProvider, Float> value) {
        this.value = NumberProviderType.resolveNoDuration(value);
    }

    @Override
    public boolean isTriggerable(Context context, UnrestrictedDataHolder tracker) {
        return true;
    }

    @Override
    public float trigger(Context context, UnrestrictedDataHolder tracker) {
        return this.value.floatValue() * context.getOrDefault(SkillContextKeys.SKILL_GAIN_MULTIPLIER, 1.0F);
    }

    @Override
    public UnrestrictedDataHolder createDataTracker() {
        return UnrestrictedDataHolder.INSTANCE;
    }

    @Override
    public SkillTrackerType<UnrestrictedDataHolder, ?> getType() {
        return CoreSkillTrackers.UNRESTRICTED.get();
    }

    public static final class UnrestrictedDataHolder implements SkillTracker {

        public static final UnrestrictedDataHolder INSTANCE = new UnrestrictedDataHolder();
        public static final MapCodec<UnrestrictedDataHolder> CODEC = MapCodec.unit(INSTANCE);

        @Override
        public SkillTrackerType<?, ?> getType() {
            return CoreSkillTrackers.UNRESTRICTED.get();
        }
    }
}
