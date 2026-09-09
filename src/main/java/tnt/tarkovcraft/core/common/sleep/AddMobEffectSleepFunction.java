package tnt.tarkovcraft.core.common.sleep;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import tnt.tarkovcraft.core.api.SleepFunction;

public record AddMobEffectSleepFunction(MobEffectInstance template) implements SleepFunction {

    public static final MapCodec<AddMobEffectSleepFunction> CODEC = MobEffectInstance.CODEC
            .xmap(AddMobEffectSleepFunction::new, AddMobEffectSleepFunction::template).fieldOf("template");

    @Override
    public void apply(ServerPlayer player, long sleptDurationTicks) {
        MobEffectInstance instance = new MobEffectInstance(this.template);
        player.addEffect(instance);
    }

    @Override
    public MapCodec<? extends SleepFunction> codec() {
        return CODEC;
    }
}
