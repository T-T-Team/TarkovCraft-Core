package tnt.tarkovcraft.core.util.helper;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailMessageItemAttachment;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.MailSystem;
import tnt.tarkovcraft.core.util.CommonLabels;

public final class PlayerHelper {

    /**
     * Either adds the item to player inventory or tries to spawn it in world, or will be sent as a mail in case the
     * in world spawn fails.
     * @param itemStack Sent itemStack instance to be received by the player
     * @param player Target player entity
     */
    public static void giveItem(ItemStack itemStack, Player player) {
        giveItem(itemStack, player, false);
    }

    /**
     * Same as {@link PlayerHelper#giveItem(ItemStack, Player, boolean, boolean)} and the item will be sent via mail
     * in case it fails to be dropped in world
     * @param itemStack Sent itemStack instance to be received by the player
     * @param player Target player entity
     * @param receiveInMail Store the item in mail
     */
    public static void giveItem(ItemStack itemStack, Player player, boolean receiveInMail) {
        giveItem(itemStack, player, receiveInMail, true);
    }

    /**
     * Gives specified itemStack to player, either as a mail attachment or as a direct inventory transfer. If the
     * player inventory is full and cannot accept the itemstack, it is dropped as entity in world. And in case the
     * entity creation fails, it can be sent to mail instead.
     *
     * @param itemstack Sent itemStack instance to be received by player
     * @param player Target player entity
     * @param receiveInMail Store the item in mail
     * @param storeInMailOnError Store the item in mail if dropping as entity in world fails
     */
    public static void giveItem(ItemStack itemstack, Player player, boolean receiveInMail, boolean storeInMailOnError) {
        // handle completely on server side
        if (player.level().isClientSide())
            return;

        if (MailSystem.isEnabled() && receiveInMail) {
            // Sends item as mail attachment
            MailSystem.sendMessage(player, MailSource.SYSTEM, MailMessage.system(CommonLabels.MESSAGE_ITEM_RECEIVED)
                    .attachment(MailMessageItemAttachment.item(itemstack.copy()))
            );
        } else {
            // Send item directly to inventory, or drop in world as entity
            if (!player.addItem(itemstack)) {
                Vec3 pos = player.position();
                ItemEntity itemEntity = new ItemEntity(player.level(), pos.x(), pos.y(), pos.z(), itemstack.copy());
                itemEntity.setTarget(player.getUUID());
                itemEntity.setNoPickUpDelay();
                if (!player.level().addFreshEntity(itemEntity)) {
                    // Failed to spawn entity, try to send as mail if enabled
                    if (MailSystem.isEnabled() && storeInMailOnError) {
                        MailSystem.sendMessage(player, MailSource.SYSTEM, MailMessage.system(CommonLabels.MESSAGE_ITEM_RECEIVED)
                                .attachment(MailMessageItemAttachment.item(itemstack.copy()))
                        );
                        TarkovCraftCore.LOGGER.error("Failed to create item drop entity {} for player {}, sending as mail instead", itemEntity, player);
                        return;
                    }
                    TarkovCraftCore.LOGGER.error("Failed to create item drop entity {} for player {}", itemEntity, player);
                }
            }
        }
    }
}
