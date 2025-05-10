package tnt.tarkovcraft.core.common.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.network.Synchronizable;

public class MovementStamina extends AbstractStamina implements Synchronizable<MovementStamina> {

    public static final Codec<MovementStamina> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("stamina", (float) DEFAULT_STAMINA_VALUE).forGetter(t -> t.stamina),
            Codec.INT.optionalFieldOf("recoveryTime", 0).forGetter(t -> t.recoveryDelay)
    ).apply(instance, MovementStamina::new));

    public MovementStamina() {
        this(DEFAULT_STAMINA_VALUE, 0);
    }

    public MovementStamina(float stamina, int recoveryDelay) {
        super(stamina, recoveryDelay);
    }

    public boolean canSprint(LivingEntity entity) {
        Boolean eventResult = EnergySystem.canSprint(this, entity);
        if (eventResult != null) {
            return eventResult;
        }
        if (!EnergySystem.isEnabled()) {
            return true;
        }
        return this.hasStamina(LOW_STAMINA);
    }

    public boolean canJump(LivingEntity entity) {
        Boolean eventResult = EnergySystem.canJump(this, entity);
        if (eventResult != null) {
            return eventResult;
        }
        if (!EnergySystem.isEnabled()) {
            return true;
        }
        return this.hasStamina(LOW_STAMINA);
    }

    public void onSprint(LivingEntity entity) {
        float amount = TarkovCraftCore.getConfig().skillSystemConfig.sprintStaminaConsumption;
        if (amount > 0) {
            this.consume(entity, amount, 20);
            EnergySystem.onSprinted(this, entity);
        }
    }

    public void onJump(LivingEntity entity) {
        float amount = TarkovCraftCore.getConfig().skillSystemConfig.jumpStaminaConsumption;
        if (amount > 0) {
            this.consume(entity, amount, 40);
            EnergySystem.onJumped(this, entity);
        }
    }

    public void update(LivingEntity entity) {
        if (entity.isSpectator())
            return;
        if (this.recoveryDelay > 0) {
            --this.recoveryDelay;
        }
        if (entity.isSprinting()) {
            this.onSprint(entity);
        } else {
            this.recover(entity);
        }
        if (!this.hasAnyStamina() && entity.isSprinting()) {
            entity.setSprinting(false);
        }
    }

    public void recover(LivingEntity entity) {
        if (this.recoveryDelay > 0) {
            return;
        }
        float max = this.getMaxStamina(entity);
        if (this.stamina < max) {
            float recoveryAmount = Math.abs(this.getRecoveryAmount(entity));
            this.stamina = Math.min(max, this.stamina + recoveryAmount);
        }
    }

    @Override
    public float getMaxStamina(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return this.value(data, CoreAttributes.LEG_ENERGY_MAX);
    }

    @Override
    public float getConsumptionMultiplier(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return this.value(data, CoreAttributes.LEG_ENERGY_CONSUMPTION_MULTIPLIER);
    }

    @Override
    public float getRecoveryTimeMultiplier(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return this.value(data, CoreAttributes.LEG_ENERGY_RECOVERY_DELAY_MULTIPLER);
    }

    public float getRecoveryAmount(LivingEntity entity) {
        EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        return this.value(data, CoreAttributes.LEG_ENERGY_RECOVERY);
    }

    @Override
    public StaminaType getStaminaType() {
        return StaminaType.MOVEMENT;
    }

    @Override
    public Codec<MovementStamina> networkCodec() {
        return CODEC;
    }

    private float value(EntityAttributeData data, Holder<Attribute> ref) {
        return data.getAttribute(ref).floatValue();
    }
}
