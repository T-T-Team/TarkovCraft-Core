package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;

public interface StaminaComponent {

    float getStamina(LivingEntity entity);

    float getMaxStamina(LivingEntity entity);

    boolean isCriticalValue(LivingEntity entity);
}
