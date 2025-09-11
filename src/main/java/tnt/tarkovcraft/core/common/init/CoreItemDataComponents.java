package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.item.Currency;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.function.Supplier;

public final class CoreItemDataComponents {

    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> WEIGHT = REGISTRY.registerComponentType("weight", builder -> builder
            .persistent(Codecs.NON_NEGATIVE_INT)
            .networkSynchronized(ByteBufCodecs.INT)
    );
    public static final Supplier<DataComponentType<Currency>> CURRENCY = REGISTRY.registerComponentType("currency", builder -> builder
            .persistent(Currency.CODEC)
            .networkSynchronized(Currency.STREAM_CODEC)
    );
}
