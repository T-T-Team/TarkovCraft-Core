package tnt.tarkovcraft.core.common.data.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

import java.util.Objects;

public record ItemStackFilterType<F extends ItemStackFilter>(ResourceLocation identifier, MapCodec<F> codec) {

    public static final Codec<ItemStackFilter> ID_CODEC = TarkovCraftRegistries.ITEMSTACK_FILTER.byNameCodec().dispatch(ItemStackFilter::getType, ItemStackFilterType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemStackFilterType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
