package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.event.StaminaEvent;

public final class EnergySystem {

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
}
