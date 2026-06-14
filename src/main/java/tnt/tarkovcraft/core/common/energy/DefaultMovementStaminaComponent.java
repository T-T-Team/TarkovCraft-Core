package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;

public class DefaultMovementStaminaComponent implements MovementStaminaComponent {

    public static final DefaultMovementStaminaComponent INSTANCE = new DefaultMovementStaminaComponent();

    @Override
    public boolean isOverlayVisible(LivingEntity entity) {
        return false;
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
    public boolean canSprint(LivingEntity entity) {
        if (entity.getType() != EntityType.PLAYER)
            return true;
        return EnergySystem.canSprint(entity);
    }

    @Override
    public void onSprint(LivingEntity entity) {
        if (entity.getType() != EntityType.PLAYER)
            return;
        EnergySystem.onSprinted(entity);
    }

    @Override
    public boolean canJump(LivingEntity entity) {
        if (entity.getType() != EntityType.PLAYER)
            return true;
        return EnergySystem.canJump(entity);
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
