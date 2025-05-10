package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;

public final class NoMovementStaminaComponent implements MovementStaminaComponent {

    public static final NoMovementStaminaComponent INSTANCE = new NoMovementStaminaComponent();

    @Override
    public boolean isActiveForEntity(LivingEntity entity) {
        return false;
    }

    @Override
    public void tick(LivingEntity entity) {
        if (entity.getType() == EntityType.PLAYER && entity.isSprinting()) {
            EnergySystem.onSprinted(entity);
        }
    }

    @Override
    public void setStamina(LivingEntity entity, float amount) {
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
    public void consumeStamina(LivingEntity entity, float amount) {
    }

    @Override
    public void recoverStamina(LivingEntity entity, float amount) {
    }

    @Override
    public boolean hasStamina(LivingEntity entity, float requiredAmount) {
        return true;
    }

    @Override
    public boolean hasAnyStamina(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canSprint(LivingEntity entity) {
        if (entity.getType() != EntityType.PLAYER)
            return true;
        Boolean sprintResult = EnergySystem.canSprint(entity);
        return sprintResult != null ? sprintResult : true;
    }

    @Override
    public boolean canJump(LivingEntity entity) {
        if (entity.getType() != EntityType.PLAYER)
            return true;
        Boolean jumpResult = EnergySystem.canJump(entity);
        return jumpResult != null ? jumpResult : true;
    }

    @Override
    public void onJump(LivingEntity entity) {
        if (entity.getType() == EntityType.PLAYER) {
            EnergySystem.onJumped(entity);
        }
    }

    @Override
    public boolean isCriticalValue(LivingEntity entity) {
        return false;
    }
}
