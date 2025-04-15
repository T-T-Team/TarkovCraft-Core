package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.data.filter.ItemStackFilterType;

public final class CoreItemStackFilters {

    public static final DeferredRegister<ItemStackFilterType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.ITEMSTACK_FILTER, TarkovCraftCore.MOD_ID);
}
