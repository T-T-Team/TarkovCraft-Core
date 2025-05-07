package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

public final class VanillaMovementStaminaComponent implements MovementStaminaComponent {

    public static final VanillaMovementStaminaComponent INSTANCE = new VanillaMovementStaminaComponent();

    @Override
    public boolean isAttached(LivingEntity entity) {
        return entity.hasData(CoreDataAttachments.MOVEMENT_STAMINA);
    }

    @Override
    public AbstractStamina getStaminaData(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.MOVEMENT_STAMINA);
    }

    @Override
    public void update(LivingEntity entity) {
        entity.getData(CoreDataAttachments.MOVEMENT_STAMINA).update(entity);
    }

    @Override
    public float getStamina(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.MOVEMENT_STAMINA).getStamina();
    }

    @Override
    public float getMaxStamina(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.MOVEMENT_STAMINA).getMaxStamina(entity);
    }

    @Override
    public void setStamina(LivingEntity entity, int amount) {
        entity.getData(CoreDataAttachments.MOVEMENT_STAMINA).setStamina(entity, amount);
    }

    @Override
    public boolean canSprint(LivingEntity entity) {
        MovementStamina stamina = entity.getData(CoreDataAttachments.MOVEMENT_STAMINA);
        return stamina.canSprint(entity);
    }

    @Override
    public boolean canJump(LivingEntity entity) {
        MovementStamina stamina = entity.getData(CoreDataAttachments.MOVEMENT_STAMINA);
        return stamina.canJump(entity);
    }

    @Override
    public void onJump(LivingEntity entity) {
        MovementStamina stamina = entity.getData(CoreDataAttachments.MOVEMENT_STAMINA);
        stamina.onJump(entity);
    }

    @Override
    public boolean isCriticalValue(LivingEntity entity) {
        return this.getStamina(entity) < MovementStamina.LOW_STAMINA;
    }
}
