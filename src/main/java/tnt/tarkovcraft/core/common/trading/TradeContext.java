package tnt.tarkovcraft.core.common.trading;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.util.context.OperationContext;

public record TradeContext(Container tradeInventory, Player player, OperationContext additionalData) {
}
