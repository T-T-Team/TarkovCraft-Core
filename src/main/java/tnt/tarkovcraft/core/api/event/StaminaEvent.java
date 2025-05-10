package tnt.tarkovcraft.core.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.api.StaminaComponent;

public abstract class StaminaEvent extends Event {

    private final StaminaComponent stamina;
    private final LivingEntity entity;

    public StaminaEvent(StaminaComponent stamina, LivingEntity entity) {
        this.stamina = stamina;
        this.entity = entity;
    }

    public StaminaComponent getStamina() {
        return stamina;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public static final class CanSprint extends StaminaEvent {

        private Boolean result;

        public CanSprint(MovementStaminaComponent stamina, LivingEntity entity) {
            super(stamina, entity);
        }

        @Override
        public MovementStaminaComponent getStamina() {
            return (MovementStaminaComponent) super.getStamina();
        }

        public void setCanSprint(Boolean result) {
            this.result = result;
        }

        public Boolean canSprint() {
            return result;
        }
    }

    public static final class AfterSprint extends StaminaEvent {

        public AfterSprint(MovementStaminaComponent stamina, LivingEntity entity) {
            super(stamina, entity);
        }

        @Override
        public MovementStaminaComponent getStamina() {
            return (MovementStaminaComponent) super.getStamina();
        }
    }

    public static final class CanJump extends StaminaEvent {

        private Boolean result;

        public CanJump(MovementStaminaComponent stamina, LivingEntity entity) {
            super(stamina, entity);
        }

        @Override
        public MovementStaminaComponent getStamina() {
            return (MovementStaminaComponent) super.getStamina();
        }

        public void setCanJump(Boolean result) {
            this.result = result;
        }

        public Boolean canJump() {
            return result;
        }
    }

    public static final class AfterJump extends StaminaEvent {

        public AfterJump(MovementStaminaComponent stamina, LivingEntity entity) {
            super(stamina, entity);
        }

        @Override
        public MovementStaminaComponent getStamina() {
            return (MovementStaminaComponent) super.getStamina();
        }
    }

    public static final class Consuming extends StaminaEvent {

        private float consumeAmount;

        public Consuming(StaminaComponent stamina, LivingEntity entity, float consumeAmount) {
            super(stamina, entity);
            this.consumeAmount = consumeAmount;
        }

        public float getConsumeAmount() {
            return consumeAmount;
        }

        public void setConsumeAmount(float consumeAmount) {
            this.consumeAmount = consumeAmount;
        }
    }

    public static final class SetRecoveryDelay extends StaminaEvent {

        private int recoveryDelay;
        private final boolean wasEnergyDrained;

        public SetRecoveryDelay(StaminaComponent stamina, LivingEntity entity, int recoveryDelay, boolean wasEnergyDrained) {
            super(stamina, entity);
            this.recoveryDelay = recoveryDelay;
            this.wasEnergyDrained = wasEnergyDrained;
        }

        /**
         * Set to -1 to disable delay
         * @param recoveryDelay New recovery delay to be set
         */
        public void setRecoveryDelay(int recoveryDelay) {
            this.recoveryDelay = recoveryDelay;
        }

        public int getRecoveryDelay() {
            return recoveryDelay;
        }

        public boolean hadFullyDrainedEnergy() {
            return wasEnergyDrained;
        }
    }
}
