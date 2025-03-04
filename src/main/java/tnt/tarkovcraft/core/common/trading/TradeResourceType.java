package tnt.tarkovcraft.core.common.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public record TradeResourceType<TR extends TradeResource>(ResourceLocation identifier, MapCodec<TR> codec) {

    public static final Codec<TradeResource> ID_CODEC = TarkovCraftRegistries.TRADE_RESOURCE.byNameCodec().dispatch(TradeResource::getType, TradeResourceType::codec);
}
