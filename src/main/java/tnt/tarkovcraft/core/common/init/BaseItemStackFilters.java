package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.filter.itemstack.ItemStackFilterType;

public final class BaseItemStackFilters {

    public static final DeferredRegister<ItemStackFilterType<?>> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.ITEMSTACK_FILTER, TarkovCraftCore.MOD_ID);
}
