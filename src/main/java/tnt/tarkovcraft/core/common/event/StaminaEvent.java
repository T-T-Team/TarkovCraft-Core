package tnt.tarkovcraft.core.common.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.common.energy.MovementStamina;

public abstract class StaminaEvent extends Event {

    private final MovementStamina stamina;
    private final LivingEntity entity;

    public StaminaEvent(MovementStamina stamina, LivingEntity entity) {
        this.stamina = stamina;
        this.entity = entity;
    }

    public MovementStamina getStamina() {
        return stamina;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public static final class CanSprint extends StaminaEvent {

        private Boolean result;

        public CanSprint(MovementStamina stamina, LivingEntity entity) {
            super(stamina, entity);
        }

        public void setCanSprint(Boolean result) {
            this.result = result;
        }

        public Boolean canSprint() {
            return result;
        }
    }

    public static final class CanJump extends StaminaEvent {

        private Boolean result;

        public CanJump(MovementStamina stamina, LivingEntity entity) {
            super(stamina, entity);
        }

        public void setCanJump(Boolean result) {
            this.result = result;
        }

        public Boolean canJump() {
            return result;
        }
    }
}
