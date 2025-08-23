package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.function.Supplier;

public final class CoreItemDataComponents {

    public static final DeferredRegister<DataComponentType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> WEIGHT = REGISTRY.register("weight", () -> DataComponentType.<Integer>builder()
            .persistent(Codecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build()
    );
}
