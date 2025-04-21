package tnt.tarkovcraft.core.common.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.network.Synchronizable;

public class MovementStamina implements Synchronizable<MovementStamina> {

    public static final Codec<MovementStamina> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("maxLevel").forGetter(t -> t.maxLevelAttribute),
            CoreRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("consumption").forGetter(t -> t.consumptionAttribute),
            CoreRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("recovery").forGetter(t -> t.recoveryAttribute),
            CoreRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("recoveryDelay").forGetter(t -> t.recoveryDelayAttribute),
            Codec.FLOAT.fieldOf("stamina").forGetter(t -> t.stamina),
            Codec.INT.fieldOf("recoveryTime").forGetter(t -> t.recoveryDelay)
    ).apply(instance, MovementStamina::new));

    public static final int DEFAULT_STAMINA_VALUE = 100;
    public static final float SPRINT_STAMINA_CONSUMPTION = 0.5F;
    public static final float JUMP_STAMINA_CONSUMPTION = 10.0F;
    public static final float ENERGY_LOW_VALUE = 20.0F;

    private final Holder<Attribute> maxLevelAttribute; // max stamina value
    private final Holder<Attribute> consumptionAttribute; // consumption multiplier
    private final Holder<Attribute> recoveryAttribute; // recovery amount / tick
    private final Holder<Attribute> recoveryDelayAttribute; // recovery time multiplier
    private float stamina; // current stamina amount
    private int recoveryDelay; // current delay before recovering stamina

    public MovementStamina() {
        this(
                CoreAttributes.LEG_ENERGY_MAX,
                CoreAttributes.LEG_ENERGY_CONSUMPTION_MULTIPLIER,
                CoreAttributes.LEG_ENERGY_RECOVERY,
                CoreAttributes.LEG_ENERGY_RECOVERY_DELAY_MULTIPLER,
                DEFAULT_STAMINA_VALUE,
                0
        );
    }

    public MovementStamina(Holder<Attribute> maxLevelAttribute, Holder<Attribute> consumptionAttribute, Holder<Attribute> recoveryAttribute, Holder<Attribute> recoveryDelayAttribute, float stamina, int recoveryDelay) {
        this.maxLevelAttribute = maxLevelAttribute;
        this.consumptionAttribute = consumptionAttribute;
        this.recoveryAttribute = recoveryAttribute;
        this.recoveryDelayAttribute = recoveryDelayAttribute;
        this.stamina = stamina;
        this.recoveryDelay = recoveryDelay;
    }

    public boolean canSprint(LivingEntity entity) {
        Boolean eventResult = EnergySystem.canSprint(this, entity);
        if (eventResult != null) {
            return eventResult;
        }
        if (!EnergySystem.isEnabled()) {
            return true;
        }
        return this.hasStamina(ENERGY_LOW_VALUE);
    }

    public boolean canJump(LivingEntity entity) {
        Boolean eventResult = EnergySystem.canJump(this, entity);
        if (eventResult != null) {
            return eventResult;
        }
        if (!EnergySystem.isEnabled()) {
            return true;
        }
        return this.hasStamina(ENERGY_LOW_VALUE);
    }

    public void onSprint(LivingEntity entity) {
        this.consume(entity, SPRINT_STAMINA_CONSUMPTION, 20);
    }

    public void onJump(LivingEntity entity) {
        this.consume(entity, JUMP_STAMINA_CONSUMPTION, 40);
    }

    public void update(LivingEntity entity) {
        if (this.recoveryDelay > 0) {
            --this.recoveryDelay;
        }
        if (entity.isSprinting()) {
            this.onSprint(entity);
        } else {
            EntityAttributeData data = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
            this.recover(data);
        }
        if (!this.hasAnyStamina() && entity.isSprinting()) {
            entity.setSprinting(false);
        }
    }

    public boolean hasStamina(float requiredValue) {
        return this.stamina >= requiredValue;
    }

    public boolean hasAnyStamina() {
        return this.stamina > 0;
    }

    public float getStamina() {
        return stamina;
    }

    public void setStamina(EntityAttributeData data, float value) {
        float max = this.getMaxStamina(data);
        this.stamina = Mth.clamp(value, 0.0F, max);
    }

    public void setRecoveryDelay(int recoveryDelay) {
        this.recoveryDelay = recoveryDelay;
    }

    public void recover(EntityAttributeData data) {
        if (this.recoveryDelay > 0) {
            return;
        }
        float max = this.getMaxStamina(data);
        if (this.stamina < max) {
            float recoveryAmount = Math.abs(this.getRecoveryAmount(data));
            this.stamina = Math.min(max, this.stamina + recoveryAmount);
        }
    }

    public void consume(LivingEntity entity, float amount, int recoveryDelay) {
        this.consume(entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES), entity, amount, recoveryDelay);
    }

    public void consume(EntityAttributeData data, LivingEntity entity, float amount, int recoveryDelay) {
        if (EnergySystem.isEnabled()) {
            float consumptionMultiplier = Math.abs(this.getConsumptionMultiplier(data));
            float consumedAmount = EnergySystem.consumeEnergy(this, entity, amount * consumptionMultiplier);
            this.setStamina(data, this.stamina - consumedAmount);
            int delay = Mth.ceil(recoveryDelay * data.getAttribute(this.recoveryDelayAttribute).floatValue());
            boolean wasDrained = false;
            if (!this.hasAnyStamina()) {
                delay *= 2;
                wasDrained = true;
            }
            int recDelay = EnergySystem.getRecoveryDelay(this, entity, delay, wasDrained);
            this.setRecoveryDelay(recDelay == -1 ? 0 : Math.max(recDelay, this.recoveryDelay));
        }
    }

    public float getMaxStamina(EntityAttributeData data) {
        return this.value(data, this.maxLevelAttribute);
    }

    public float getRecoveryAmount(EntityAttributeData data) {
        return this.value(data, this.recoveryAttribute);
    }

    public float getConsumptionMultiplier(EntityAttributeData data) {
        return this.value(data, this.consumptionAttribute);
    }

    @Override
    public Codec<MovementStamina> networkCodec() {
        return CODEC;
    }

    private float value(EntityAttributeData data, Holder<Attribute> ref) {
        return data.getAttribute(ref).floatValue();
    }
}
