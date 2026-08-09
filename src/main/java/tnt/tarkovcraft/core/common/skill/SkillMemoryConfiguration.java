package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;

public record SkillMemoryConfiguration(boolean canLoseLevel, int startAfter, float experienceLoss) {

    public static final Codec<SkillMemoryConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("level_loss", false).forGetter(SkillMemoryConfiguration::canLoseLevel),
            NumberProvider.DURATION.fieldOf("start_after").forGetter(t -> t.startAfter),
            NumberProvider.POSITIVE_FLOAT.fieldOf("experience_loss").forGetter(t -> t.experienceLoss)
    ).apply(instance, SkillMemoryConfiguration::new));
    public static final SkillMemoryConfiguration NO_LOSS = new SkillMemoryConfiguration(false, 0, 0.0F);

    public boolean isEnabled() {
        return this.startAfter > 0L && this.experienceLoss > 0;
    }
}
