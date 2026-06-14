package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.api.ArmStaminaComponent;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.api.StaminaComponent;
import tnt.tarkovcraft.core.api.event.CoreEventHooks;
import tnt.tarkovcraft.core.api.event.StaminaEvent;
import tnt.tarkovcraft.core.compatibility.Component;

public final class EnergySystem {

    public static final Component<MovementStaminaComponent> MOVEMENT_STAMINA = new Component<>("Movement Stamina", DefaultMovementStaminaComponent.INSTANCE);
    public static final Component<ArmStaminaComponent> ARM_STAMINA = new Component<>("Arm Stamina", NoArmStaminaComponent.INSTANCE);

    public static boolean canSprint(LivingEntity entity) {
        return CoreEventHooks.canEntitySprint(MOVEMENT_STAMINA.getComponent(), entity);
    }

    public static void onSprinted(LivingEntity entity) {
        CoreEventHooks.onEntitySprint(MOVEMENT_STAMINA.getComponent(), entity);
    }

    public static boolean canJump(LivingEntity entity) {
        return CoreEventHooks.canEntityJump(MOVEMENT_STAMINA.getComponent(), entity);
    }

    public static void onJumped(LivingEntity entity) {
        CoreEventHooks.onEntityJump(MOVEMENT_STAMINA.getComponent(), entity);
    }

    public static float consumeEnergy(StaminaComponent component, LivingEntity entity, float baseConsumption) {
        return CoreEventHooks.consumeStamina(component, entity, baseConsumption);
    }

    public static float recoverEnergy(StaminaComponent component, LivingEntity entity, float baseRecovery) {
        return CoreEventHooks.recoverStamina(component, entity, baseRecovery);
    }
}
