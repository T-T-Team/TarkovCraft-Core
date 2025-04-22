package tnt.tarkovcraft.core.common.energy;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractStamina {

    public static final int DEFAULT_STAMINA_VALUE = 100;
    public static final float LOW_STAMINA = 20.0F;

    protected float stamina;
    protected int recoveryDelay;

    public AbstractStamina(float stamina, int recoveryDelay) {
        this.stamina = stamina;
        this.recoveryDelay = recoveryDelay;
    }

    public abstract float getMaxStamina(LivingEntity entity);

    public abstract StaminaType getStaminaType();

    public abstract float getConsumptionMultiplier(LivingEntity entity);

    public abstract float getRecoveryTimeMultiplier(LivingEntity entity);

    public boolean hasStamina(float amount) {
        return this.getStamina() >= amount;
    }

    public boolean hasAnyStamina() {
        return this.getStamina() > 0;
    }

    public float getStamina() {
        return this.stamina;
    }

    public void setStamina(LivingEntity entity, float stamina) {
        float max = this.getMaxStamina(entity);
        this.stamina = Mth.clamp(stamina, 0, max);
    }

    public void setRecoveryDelay(int recoveryDelay) {
        this.recoveryDelay = recoveryDelay;
    }

    public void consume(LivingEntity entity, float amount, int recoveryDelay) {
        if (EnergySystem.isEnabled()) {
            float multiplier = this.getConsumptionMultiplier(entity);
            float consumeAmount = EnergySystem.consumeEnergy(this, entity, amount * multiplier);
            if (consumeAmount > 0) {
                this.setStamina(entity, this.getStamina() - consumeAmount);
                this.onConsume(entity, consumeAmount, recoveryDelay);
            }
        }
    }

    protected void onConsume(LivingEntity entity, float consumedAmount, int recoveryDelay) {
        float recoveryMultiplier = this.getRecoveryTimeMultiplier(entity);
        int baseRecoveryTime = Mth.ceil(recoveryDelay * recoveryMultiplier);
        boolean hasStamina = this.hasAnyStamina();
        int recoveryTime = hasStamina ? baseRecoveryTime : this.getFullRecoveryTime(entity, baseRecoveryTime);
        int finalRecoveryTime = EnergySystem.getRecoveryDelay(this, entity, recoveryTime, !hasStamina);
        this.setRecoveryDelay(finalRecoveryTime < 0 ? 0 : Math.max(this.recoveryDelay, finalRecoveryTime));
    }

    protected int getFullRecoveryTime(LivingEntity entity, int initialTime) {
        return initialTime * 2;
    }
}
