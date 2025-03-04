package tnt.tarkovcraft.core.common.init.filter.itemstack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public record ItemStackFilterType<F extends ItemStackFilter>(ResourceLocation identifier, MapCodec<F> codec) {

    public static final Codec<ItemStackFilter> ID_CODEC = TarkovCraftRegistries.ITEMSTACK_FILTER.byNameCodec().dispatch(ItemStackFilter::getType, ItemStackFilterType::codec);
}
