package tnt.tarkovcraft.core.common.sleep;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import tnt.tarkovcraft.core.api.SleepFunction;

public record AddHealthSleepFunction(float healthAmount) implements SleepFunction {

    public static final MapCodec<AddHealthSleepFunction> CODEC = Codec.FLOAT
            .xmap(AddHealthSleepFunction::new, AddHealthSleepFunction::healthAmount).fieldOf("amount");

    @Override
    public void apply(ServerPlayer player, long sleptDurationTicks) {
        player.heal(this.healthAmount);
    }

    @Override
    public MapCodec<? extends SleepFunction> codec() {
        return CODEC;
    }
}
