package tnt.tarkovcraft.core.common.sleep;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.api.SleepFunction;

public record SleepBonus(int requiredSleepDuration, SleepFunction function) {

    public static final Codec<SleepBonus> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.optionalFieldOf("required_duration", 1).forGetter(SleepBonus::requiredSleepDuration),
            SleepFunction.CODEC.fieldOf("function").forGetter(SleepBonus::function)
    ).apply(instance, SleepBonus::new));
}
