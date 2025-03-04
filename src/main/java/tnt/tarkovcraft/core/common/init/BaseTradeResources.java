package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.trading.ItemResource;
import tnt.tarkovcraft.core.common.trading.TradeResourceType;

import java.util.function.Supplier;

public final class BaseTradeResources {

    public static final DeferredRegister<TradeResourceType<?>> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.TRADE_RESOURCE, TarkovCraftCore.MOD_ID);

    public static final Supplier<TradeResourceType<ItemResource>> ITEM_RESOURCE = REGISTRY.register("item", (key) -> new TradeResourceType<>(key, ItemResource.CODEC));
}
