package tnt.tarkovcraft.core.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface LeftClickListener {
    void onLeftClick(Player player, Level level, ItemStack itemStack, @Nullable BlockPos interactAt);
}
