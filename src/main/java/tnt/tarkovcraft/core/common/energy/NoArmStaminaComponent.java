package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.ArmStaminaComponent;

public class NoArmStaminaComponent implements ArmStaminaComponent {

    public static final NoArmStaminaComponent INSTANCE = new NoArmStaminaComponent();

    @Override
    public boolean isActiveForEntity(LivingEntity entity) {
        return false;
    }

    @Override
    public void tick(LivingEntity entity) {
    }

    @Override
    public float getStamina(LivingEntity entity) {
        return 0;
    }

    @Override
    public void setStamina(LivingEntity entity, float amount) {
    }

    @Override
    public void consumeStamina(LivingEntity entity, float amount) {
    }

    @Override
    public void recoverStamina(LivingEntity entity, float amount) {
    }

    @Override
    public float getMaxStamina(LivingEntity entity) {
        return 0;
    }

    @Override
    public boolean hasAnyStamina(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean hasStamina(LivingEntity entity, float requiredAmount) {
        return true;
    }

    @Override
    public boolean isCriticalValue(LivingEntity entity) {
        return false;
    }
}
