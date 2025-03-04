package tnt.tarkovcraft.core.common.trading;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;

public record TradeContext(Container tradeInventory, Player player, TradeSource source) {
}
