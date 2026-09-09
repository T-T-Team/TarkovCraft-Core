package tnt.tarkovcraft.core.common.sleep;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import tnt.tarkovcraft.core.api.SleepFunction;

public record AddHealthSleepFunction(int cycleLength, float amount) implements SleepFunction {

    public static final MapCodec<AddHealthSleepFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("cycle_length").forGetter(AddHealthSleepFunction::cycleLength),
            Codec.FLOAT.fieldOf("amount").forGetter(AddHealthSleepFunction::amount)
    ).apply(instance, AddHealthSleepFunction::new));

    @Override
    public void apply(ServerPlayer player, long sleptDurationTicks) {
        int cycles = (int) (sleptDurationTicks / this.cycleLength);
        float healAmount = cycles * this.amount;
        player.heal(healAmount);
    }

    @Override
    public MapCodec<? extends SleepFunction> codec() {
        return CODEC;
    }
}
