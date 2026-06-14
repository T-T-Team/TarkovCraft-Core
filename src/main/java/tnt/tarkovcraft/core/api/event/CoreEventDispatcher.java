package tnt.tarkovcraft.core.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

import java.util.Stack;

public final class CoreEventDispatcher {

    public static void onLivingApplyDamage(LivingEntity entity, Stack<DamageContainer> damageContainers) {
        NeoForge.EVENT_BUS.post(new LivingDamageApplyEvent(entity, damageContainers.peek()));
    }
}
