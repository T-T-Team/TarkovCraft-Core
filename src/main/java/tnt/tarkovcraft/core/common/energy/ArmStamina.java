package tnt.tarkovcraft.core.common.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.network.Synchronizable;

@Deprecated
public final class ArmStamina extends AbstractStamina implements Synchronizable<ArmStamina> {

    public static final Codec<ArmStamina> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("stamina", (float) DEFAULT_STAMINA_VALUE).forGetter(t -> t.stamina),
            Codec.INT.optionalFieldOf("recovery", 0).forGetter(t -> t.recoveryDelay)
    ).apply(instance, ArmStamina::new));

    public ArmStamina() {
        this(DEFAULT_STAMINA_VALUE, 0);
    }

    public ArmStamina(float stamina, int recovery) {
        super(stamina, recovery);
    }

    public boolean checkRecoveringAndUpdate(LivingEntity entity) {
        if (this.recoveryDelay > 0) {
            this.recoveryDelay--;
            return false;
        }
        return true;
    }

    @Override
    public float getMaxStamina(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return data.getAttribute(CoreAttributes.ARM_ENERGY_MAX).floatValue();
    }

    @Override
    public float getConsumptionMultiplier(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return data.getAttribute(CoreAttributes.ARM_ENERGY_CONSUMPTION_MULTIPLIER).floatValue();
    }

    @Override
    public float getRecoveryTimeMultiplier(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return data.getAttribute(CoreAttributes.ARM_ENERGY_RECOVERY_DELAY_MULTIPLER).floatValue();
    }

    public float getRecoveryAmount(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return data.getAttribute(CoreAttributes.ARM_ENERGY_RECOVERY).floatValue();
    }

    @Override
    public StaminaType getStaminaType() {
        return StaminaType.ARM;
    }

    @Override
    public Codec<ArmStamina> networkCodec() {
        return CODEC;
    }
}
