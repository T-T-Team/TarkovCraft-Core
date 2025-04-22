package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.ArmStaminaComponent;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.event.StaminaEvent;
import tnt.tarkovcraft.core.compatibility.CompatibilityComponent;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;

public final class EnergySystem {

    public static final CompatibilityComponent<MovementStaminaComponent> MOVEMENT_STAMINA = new CompatibilityComponent<>("Movement Stamina", VanillaMovementStaminaComponent.INSTANCE);
    public static final CompatibilityComponent<ArmStaminaComponent> ARM_STAMINA = new CompatibilityComponent<>("Arm Stamina", VanillaArmStaminaComponent.INSTANCE);

    public static boolean isEnabled() {
        SkillSystemConfig config = TarkovCraftCore.getConfig().skillSystemConfig;
        return config.skillSystemEnabled && config.staminaEnabled;
    }

    public static Boolean canSprint(MovementStamina stamina, LivingEntity entity) {
        StaminaEvent.CanSprint event = NeoForge.EVENT_BUS.post(new StaminaEvent.CanSprint(stamina, entity));
        return event.canSprint();
    }

    public static Boolean canJump(MovementStamina stamina, LivingEntity entity) {
        StaminaEvent.CanJump event = NeoForge.EVENT_BUS.post(new StaminaEvent.CanJump(stamina, entity));
        return event.canJump();
    }

    public static float consumeEnergy(AbstractStamina stamina, LivingEntity entity, float baseConsumption) {
        StaminaEvent.Consuming event = NeoForge.EVENT_BUS.post(new StaminaEvent.Consuming(stamina, entity, baseConsumption));
        return Math.abs(event.getConsumeAmount());
    }

    public static int getRecoveryDelay(AbstractStamina stamina, LivingEntity entity, int recoveryDelay, boolean wasDrained) {
        StaminaEvent.SetRecoveryDelay event = NeoForge.EVENT_BUS.post(new StaminaEvent.SetRecoveryDelay(stamina, entity, recoveryDelay, wasDrained));
        return event.getRecoveryDelay();
    }
}
