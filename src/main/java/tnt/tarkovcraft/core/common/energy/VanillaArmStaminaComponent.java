package tnt.tarkovcraft.core.common.energy;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.api.ArmStaminaComponent;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

public class VanillaArmStaminaComponent implements ArmStaminaComponent {

    public static final VanillaArmStaminaComponent INSTANCE = new VanillaArmStaminaComponent();

    @Override
    public boolean isAttached(LivingEntity entity) {
        return entity.hasData(CoreDataAttachments.ARM_STAMINA);
    }

    @Override
    public boolean checkRecoveryAndUpdate(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.ARM_STAMINA).checkRecoveringAndUpdate(entity);
    }

    @Override
    public float getStamina(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.ARM_STAMINA).getStamina();
    }

    @Override
    public float getMaxStamina(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.ARM_STAMINA).getMaxStamina(entity);
    }

    @Override
    public void setStamina(LivingEntity entity, float stamina) {
        entity.getData(CoreDataAttachments.ARM_STAMINA).setStamina(entity, stamina);
    }

    @Override
    public boolean hasSufficientStamina(LivingEntity entity, float amount) {
        return entity.getData(CoreDataAttachments.ARM_STAMINA).hasStamina(amount);
    }

    @Override
    public boolean isCriticalValue(LivingEntity entity) {
        return !this.hasSufficientStamina(entity, AbstractStamina.LOW_STAMINA);
    }
}
