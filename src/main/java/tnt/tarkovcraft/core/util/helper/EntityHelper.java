package tnt.tarkovcraft.core.util.helper;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import tnt.tarkovcraft.core.TarkovCraftCore;

import javax.annotation.Nullable;

public final class EntityHelper {

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

    public static void dropEquippedItem(LivingEntity entity, EquipmentSlot slot) {
        Level level = entity.level();
        if (level.isClientSide())
            return;
        ItemStack stack = entity.getItemBySlot(slot);
        if (entity instanceof Player player) {
            player.drop(stack, true, false);
            player.setItemSlot(slot, ItemStack.EMPTY);
        } else {
            drop(entity, stack, true, false);
        }
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

    public static void displayClientMessage(Entity entity, Component message, boolean actionBar) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        player.displayClientMessage(message, actionBar);
    }

    public static void displayClientMessage(Entity entity, Component message) {
        displayClientMessage(entity, message, true);
    }

    private static @Nullable ItemEntity drop(LivingEntity entity, ItemStack droppedItem, boolean dropAround, boolean includeThrowerName) {
        if (droppedItem.isEmpty()) {
            return null;
        } else {
            if (entity.level().isClientSide) {
                entity.swing(InteractionHand.MAIN_HAND);
            }

            double d0 = entity.getEyeY() - 0.3F;
            ItemEntity itementity = new ItemEntity(entity.level(), entity.getX(), d0, entity.getZ(), droppedItem);
            itementity.setPickUpDelay(40);
            if (includeThrowerName) {
                itementity.setThrower(entity);
            }

            if (dropAround) {
                float f = entity.getRandom().nextFloat() * 0.5F;
                float f1 = entity.getRandom().nextFloat() * (float) (Math.PI * 2);
                itementity.setDeltaMovement(-Mth.sin(f1) * f, 0.2F, Mth.cos(f1) * f);
            } else {
                float f8 = Mth.sin(entity.getXRot() * (float) (Math.PI / 180.0));
                float f2 = Mth.cos(entity.getXRot() * (float) (Math.PI / 180.0));
                float f3 = Mth.sin(entity.getYRot() * (float) (Math.PI / 180.0));
                float f4 = Mth.cos(entity.getYRot() * (float) (Math.PI / 180.0));
                float f5 = entity.getRandom().nextFloat() * (float) (Math.PI * 2);
                float f6 = 0.02F * entity.getRandom().nextFloat();
                itementity.setDeltaMovement(
                        (double)(-f3 * f2 * 0.3F) + Math.cos(f5) * (double)f6,
                        -f8 * 0.3F + 0.1F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1F,
                        (double)(f4 * f2 * 0.3F) + Math.sin(f5) * (double)f6
                );
            }

            return itementity;
        }
    }
}
