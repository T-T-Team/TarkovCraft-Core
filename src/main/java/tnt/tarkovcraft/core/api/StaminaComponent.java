package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.energy.AbstractStamina;

public interface StaminaComponent {

    boolean isAttached(LivingEntity entity);

    AbstractStamina getStaminaData(LivingEntity entity);

    float getStamina(LivingEntity entity);

    float getMaxStamina(LivingEntity entity);

    boolean isCriticalValue(LivingEntity entity);
}
