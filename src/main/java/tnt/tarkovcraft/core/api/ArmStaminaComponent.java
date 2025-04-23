package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;

public interface ArmStaminaComponent extends StaminaComponent {

    boolean checkRecoveryAndUpdate(LivingEntity entity);

    void setStamina(LivingEntity entity, float stamina);

    boolean hasSufficientStamina(LivingEntity entity, float amount);
}
