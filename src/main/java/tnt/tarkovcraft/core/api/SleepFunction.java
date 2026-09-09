package tnt.tarkovcraft.core.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.function.Function;

public interface SleepFunction {

    Codec<SleepFunction> CODEC = CoreRegistries.SLEEP_BONUS_FUNCTION.byNameCodec().dispatch(SleepFunction::codec, Function.identity());

    void apply(ServerPlayer player, long sleptDurationTicks);

    MapCodec<? extends SleepFunction> codec();
}
