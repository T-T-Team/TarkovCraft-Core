package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

public final class VanillaMovementStaminaComponent implements MovementStaminaComponent {

    public static final VanillaMovementStaminaComponent INSTANCE = new VanillaMovementStaminaComponent();

    @Override
    public boolean isAttached(LivingEntity entity) {
        return entity.hasData(CoreDataAttachments.STAMINA);
    }

    @Override
    public void update(LivingEntity entity) {
        entity.getData(CoreDataAttachments.STAMINA).update(entity);
    }

    @Override
    public void setStamina(LivingEntity entity, int amount) {
        entity.getData(CoreDataAttachments.STAMINA).setStamina(entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES), amount);
    }

    @Override
    public boolean canSprint(LivingEntity entity) {
        MovementStamina stamina = entity.getData(CoreDataAttachments.STAMINA);
        return stamina.canSprint(entity);
    }

    @Override
    public boolean canJump(LivingEntity entity) {
        MovementStamina stamina = entity.getData(CoreDataAttachments.STAMINA);
        return stamina.canJump(entity);
    }

    @Override
    public void onJump(LivingEntity entity) {
        MovementStamina stamina = entity.getData(CoreDataAttachments.STAMINA);
        stamina.onJump(entity);
    }
}
