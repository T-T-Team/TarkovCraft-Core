package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.ArmStaminaComponent;

public class NoArmStaminaComponent implements ArmStaminaComponent {

    public static final NoArmStaminaComponent INSTANCE = new NoArmStaminaComponent();

    @Override
    public boolean isAttached(LivingEntity entity) {
        return false;
    }

    @Override
    public AbstractStamina getStaminaData(LivingEntity entity) {
        return null;
    }

    @Override
    public boolean checkRecoveryAndUpdate(LivingEntity entity) {
        return false;
    }

    @Override
    public float getStamina(LivingEntity entity) {
        return 0.0F;
    }

    @Override
    public float getMaxStamina(LivingEntity entity) {
        return 0.0F;
    }

    @Override
    public void setStamina(LivingEntity entity, float stamina) {
    }

    @Override
    public boolean hasSufficientStamina(LivingEntity entity, float amount) {
        return false;
    }

    @Override
    public boolean isCriticalValue(LivingEntity entity) {
        return false;
    }
}
