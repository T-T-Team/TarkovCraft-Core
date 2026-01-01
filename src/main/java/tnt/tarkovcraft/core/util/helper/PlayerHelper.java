package tnt.tarkovcraft.core.util.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import tnt.tarkovcraft.core.TarkovCraftCore;

public final class PlayerHelper {

    /**
     * Gives specified itemStack to player, either as a mail attachment or as a direct inventory transfer. If the
     * player inventory is full and cannot accept the itemstack, it is dropped as entity in world. And in case the
     * entity creation fails, it can be sent to mail instead.
     *
     * @param itemstack Sent itemStack instance to be received by player
     * @param player Target player entity
     */
    public static void giveItem(ItemStack itemstack, Player player) {
        // handle completely on server side
        if (player.level().isClientSide())
            return;

        if (!player.addItem(itemstack)) {
            Vec3 pos = player.position();
            ItemEntity itemEntity = new ItemEntity(player.level(), pos.x(), pos.y(), pos.z(), itemstack.copy());
            itemEntity.setTarget(player.getUUID());
            itemEntity.setNoPickUpDelay();
            if (!player.level().addFreshEntity(itemEntity)) {
                // Failed to spawn entity
                TarkovCraftCore.LOGGER.error(TarkovCraftCore.MARKER, "Failed to create item drop entity {} for player {}", itemEntity, player);
            }
        }
    }

    public static boolean isCreativeOrSpectator(Entity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }
}
