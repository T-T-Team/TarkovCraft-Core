package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;

public interface StaminaComponent {

    boolean isActiveForEntity(LivingEntity entity);

    void tick(LivingEntity entity);

    float getStamina(LivingEntity entity);

    void setStamina(LivingEntity entity, float amount);

    void consumeStamina(LivingEntity entity, float amount);

    void recoverStamina(LivingEntity entity, float amount);

    float getMaxStamina(LivingEntity entity);

    boolean hasStamina(LivingEntity entity, float requiredAmount);

    boolean hasAnyStamina(LivingEntity entity);

    boolean isCriticalValue(LivingEntity entity);
}
