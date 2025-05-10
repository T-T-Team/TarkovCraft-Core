package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.api.ArmStaminaComponent;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.api.StaminaComponent;
import tnt.tarkovcraft.core.api.event.StaminaEvent;
import tnt.tarkovcraft.core.compatibility.Component;

public final class EnergySystem {

    public static final Component<MovementStaminaComponent> MOVEMENT_STAMINA = new Component<>("Movement Stamina", NoMovementStaminaComponent.INSTANCE);
    public static final Component<ArmStaminaComponent> ARM_STAMINA = new Component<>("Arm Stamina", NoArmStaminaComponent.INSTANCE);

    public static Boolean canSprint(LivingEntity entity) {
        StaminaEvent.CanSprint event = NeoForge.EVENT_BUS.post(new StaminaEvent.CanSprint(MOVEMENT_STAMINA.getComponent(), entity));
        return event.canSprint();
    }

    public static void onSprinted(LivingEntity entity) {
        NeoForge.EVENT_BUS.post(new StaminaEvent.AfterSprint(MOVEMENT_STAMINA.getComponent(), entity));
    }

    public static Boolean canJump(LivingEntity entity) {
        StaminaEvent.CanJump event = NeoForge.EVENT_BUS.post(new StaminaEvent.CanJump(MOVEMENT_STAMINA.getComponent(), entity));
        return event.canJump();
    }

    public static void onJumped(LivingEntity entity) {
        NeoForge.EVENT_BUS.post(new StaminaEvent.AfterJump(MOVEMENT_STAMINA.getComponent(), entity));
    }

    public static float consumeEnergy(StaminaComponent component, LivingEntity entity, float baseConsumption) {
        StaminaEvent.Consuming event = NeoForge.EVENT_BUS.post(new StaminaEvent.Consuming(component, entity, baseConsumption));
        return Math.abs(event.getConsumeAmount());
    }

    public static int getRecoveryDelay(StaminaComponent component, LivingEntity entity, int recoveryDelay, boolean wasDrained) {
        StaminaEvent.SetRecoveryDelay event = NeoForge.EVENT_BUS.post(new StaminaEvent.SetRecoveryDelay(component, entity, recoveryDelay, wasDrained));
        return event.getRecoveryDelay();
    }
}
