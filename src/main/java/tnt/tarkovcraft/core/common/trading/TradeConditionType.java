package tnt.tarkovcraft.core.common.trading;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record TradeConditionType<TC extends TradeCondition>(ResourceLocation identifier, MapCodec<TC> codec) {
}
