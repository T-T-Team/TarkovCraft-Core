package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.event.MovementStaminaEvent;

public final class EnergySystem {

    public static boolean isEnabled() {
        SkillSystemConfig config = TarkovCraftCore.getConfig().skillSystemConfig;
        return config.skillSystemEnabled && config.staminaEnabled;
    }

    public static Boolean canSprint(MovementStamina stamina, LivingEntity entity) {
        MovementStaminaEvent.CanSprint event = NeoForge.EVENT_BUS.post(new MovementStaminaEvent.CanSprint(stamina, entity));
        return event.canSprint();
    }

    public static Boolean canJump(MovementStamina stamina, LivingEntity entity) {
        MovementStaminaEvent.CanJump event = NeoForge.EVENT_BUS.post(new MovementStaminaEvent.CanJump(stamina, entity));
        return event.canJump();
    }

    public static float consumeEnergy(MovementStamina stamina, LivingEntity entity, float baseConsumption) {
        MovementStaminaEvent.Consuming event = NeoForge.EVENT_BUS.post(new MovementStaminaEvent.Consuming(stamina, entity, baseConsumption));
        return Math.abs(event.getConsumeAmount());
    }

    public static int getRecoveryDelay(MovementStamina stamina, LivingEntity entity, int recoveryDelay, boolean wasDrained) {
        MovementStaminaEvent.SetRecoveryDelay event = NeoForge.EVENT_BUS.post(new MovementStaminaEvent.SetRecoveryDelay(stamina, entity, recoveryDelay, wasDrained));
        return event.getRecoveryDelay();
    }
}
