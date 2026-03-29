package tnt.tarkovcraft.core.util.helper;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tnt.tarkovcraft.core.TarkovCraftCore;

public final class EntityHelper {

    /**
     * Gives specified template to player, either as a mail attachment or as a direct inventory transfer. If the
     * player inventory is full and cannot accept the itemstack, it is dropped as entity in world. And in case the
     * entity creation fails, it can be sent to mail instead.
     *
     * @param itemstack Sent template instance to be received by player
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

    public static void dropEquippedItem(LivingEntity entity, EquipmentSlot slot) {
        Level level = entity.level();
        if (level.isClientSide())
            return;
        ItemStack stack = entity.getItemBySlot(slot);
        entity.drop(stack, true, false);
        entity.setItemSlot(slot, ItemStack.EMPTY);
    }

    public static boolean isCreativeOrSpectator(Entity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }

    public static void hurtOrConsumeEquipmentItem(LivingEntity entity, ItemStack itemStack, int damageAmount, EquipmentSlot slot) {
        Level level = entity.level();
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            if (itemStack.isDamageableItem()) {
                itemStack.hurtAndBreak(damageAmount, serverLevel, entity, item -> entity.onEquippedItemBroken(item, slot));
            } else {
                itemStack.consume(damageAmount, entity);
            }
        }
    }

    public static void displayClientMessage(Entity entity, Component message, boolean overlay) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        if (overlay) {
            player.sendOverlayMessage(message);
        } else {
            player.sendSystemMessage(message);
        }
    }

    public static void displayClientMessage(Entity entity, Component message) {
        displayClientMessage(entity, message, true);
    }
}
