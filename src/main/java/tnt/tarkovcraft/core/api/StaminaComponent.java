package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;

public interface StaminaComponent {

    boolean shouldRenderOverlay(LivingEntity entity);

    float getStamina(LivingEntity entity);

    void setStamina(LivingEntity entity, float amount);

    float getMaxStamina(LivingEntity entity);

    boolean isCriticalValue(LivingEntity entity);
}
