package tnt.tarkovcraft.core.common.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.BaseAttributes;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.network.Synchronizable;

public class MovementStamina implements Synchronizable<MovementStamina> {

    public static final Codec<MovementStamina> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TarkovCraftRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("maxLevel").forGetter(t -> t.maxLevelAttribute),
            TarkovCraftRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("consumption").forGetter(t -> t.consumptionAttribute),
            TarkovCraftRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("recovery").forGetter(t -> t.recoveryAttribute),
            Codec.FLOAT.fieldOf("energy").forGetter(t -> t.energy),
            Codec.INT.fieldOf("recoveryDelay").forGetter(t -> t.recoveryDelay)
    ).apply(instance, MovementStamina::new));

    public static final int DEFAULT_STAMINA_VALUE = 100;
    public static final float SPRINT_STAMINA_CONSUMPTION = 0.5F;
    public static final float JUMP_STAMINA_CONSUMPTION = 15.0F;
    public static final float ENERGY_LOW_VALUE = 20.0F;

    private final Holder<Attribute> maxLevelAttribute;
    private final Holder<Attribute> consumptionAttribute;
    private final Holder<Attribute> recoveryAttribute;
    private float energy;
    private int recoveryDelay;

    public MovementStamina() {
        this(
                BaseAttributes.LEG_ENERGY_MAX,
                BaseAttributes.LEG_ENERGY_CONSUMPTION_MULTIPLIER,
                BaseAttributes.LEG_ENERGY_RECOVERY,
                DEFAULT_STAMINA_VALUE,
                0
        );
    }

    public MovementStamina(Holder<Attribute> maxLevelAttribute, Holder<Attribute> consumptionAttribute, Holder<Attribute> recoveryAttribute, float energy, int recoveryDelay) {
        this.maxLevelAttribute = maxLevelAttribute;
        this.consumptionAttribute = consumptionAttribute;
        this.recoveryAttribute = recoveryAttribute;
        this.energy = energy;
    }

    public boolean canSprint(LivingEntity entity) {
        EntityAttributeData data = entity.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        return data.getAttribute(BaseAttributes.SPRINT).booleanValue() && this.hasEnergy(SPRINT_STAMINA_CONSUMPTION);
    }

    public boolean canJump(LivingEntity entity) {
        return this.hasEnergy(ENERGY_LOW_VALUE);
    }

    public void onSprint(LivingEntity entity) {
        EntityAttributeData data = entity.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        this.consume(data, SPRINT_STAMINA_CONSUMPTION, 20); // TODO multiplier for recovery
    }

    public void onJump(LivingEntity entity) {
        EntityAttributeData data = entity.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        this.consume(data, JUMP_STAMINA_CONSUMPTION, 40); // TODO multiplier for recovery
    }

    public void update(LivingEntity entity) {
        if (this.recoveryDelay > 0) {
            --this.recoveryDelay;
            return;
        }
        if (entity.isSprinting()) {
            this.onSprint(entity);
        } else {
            EntityAttributeData data = entity.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
            this.recover(data);
        }
        if (!this.hasAnyEnergy() && entity.isSprinting()) {
            entity.setSprinting(false);
        }
    }

    public boolean hasEnergy(float requiredValue) {
        return this.energy >= requiredValue;
    }

    public boolean hasAnyEnergy() {
        return this.energy > 0;
    }

    public float getEnergy() {
        return energy;
    }

    public void set(EntityAttributeData data, float value) {
        float max = this.getMaxEnergy(data);
        this.energy = Mth.clamp(value, 0.0F, max);
    }

    public void setRecoveryDelay(int recoveryDelay) {
        this.recoveryDelay = recoveryDelay;
    }

    public void recover(EntityAttributeData data) {
        if (this.recoveryDelay > 0) {
            return;
        }
        float max = this.getMaxEnergy(data);
        if (this.energy < max) {
            float recoveryAmount = Math.abs(this.getRecoveryAmount(data));
            this.energy = Math.min(max, this.energy + recoveryAmount);
        }
    }

    public void consume(EntityAttributeData data, float amount, int recoveryDelay) {
        float consumptionMultiplier = Math.abs(this.getConsumptionMultiplier(data));
        this.set(data, this.energy - amount * consumptionMultiplier);
        this.setRecoveryDelay(recoveryDelay);
    }

    public float getMaxEnergy(EntityAttributeData data) {
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
