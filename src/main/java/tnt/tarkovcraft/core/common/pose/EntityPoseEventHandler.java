package tnt.tarkovcraft.core.common.pose;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class EntityPoseEventHandler {

    @SubscribeEvent
    private void onItemUse(PlayerInteractEvent.RightClickItem event) {
        this.cancelInteraction(event);
    }

    @SubscribeEvent
    private void onBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        this.cancelInteraction(event);
    }

    @SubscribeEvent
    private void onBlockAttack(PlayerInteractEvent.LeftClickBlock event) {
        this.cancelInteraction(event);
    }

    @SubscribeEvent
    private void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        this.cancelInteraction(event);
    }

    @SubscribeEvent
    private void onEntitySpecificInteract(PlayerInteractEvent.EntityInteract event) {
        this.cancelInteraction(event);
    }

    @SubscribeEvent
    private void onEntityKnockback(LivingKnockBackEvent event) {
        if (EntityPoseManager.isTagged(event.getEntity(), CoreEntityPoseFlags.NO_KNOCKBACK)) {
            event.setCanceled(true);
        }
    }

    private <T extends PlayerInteractEvent & ICancellableEvent> void cancelInteraction(T event) {
        Player entity = event.getEntity();
        if (EntityPoseManager.isTagged(entity, CoreEntityPoseFlags.NO_INTERACTION)) {
            event.setCanceled(true);
        }
    }
}
