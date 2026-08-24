package tnt.tarkovcraft.core.api.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.util.UserActionResult;

import java.util.Objects;

public abstract class EntityInteractionEvent extends Event {

    private final EntityInteraction.Type<?> interactionType;
    private final EntityInteraction.Context interactionContext;

    public EntityInteractionEvent(EntityInteraction.Type<?> interactionType, EntityInteraction.Context interactionContext) {
        this.interactionType = interactionType;
        this.interactionContext = interactionContext;
    }

    public final EntityInteraction.Type<?> getInteractionType() {
        return interactionType;
    }

    public final boolean isSelfInteraction() {
        return this.interactionContext.isSelfInteraction();
    }

    public final Player getPlayer() {
        return this.interactionContext.player();
    }

    public final LivingEntity getInteractionTarget() {
        return this.interactionContext.target();
    }

    public final ItemStack getInteractionItem() {
        return this.interactionContext.itemStack();
    }

    public final EntityInteraction.Context getInteractionContext() {
        return interactionContext;
    }

    public static final class OnAvailabilityCheck extends EntityInteractionEvent {

        private UserActionResult<Void> result;

        public OnAvailabilityCheck(EntityInteraction.Type<?> interactionType, EntityInteraction.Context interactionContext, UserActionResult<Void> result) {
            super(interactionType, interactionContext);
            this.result = result;
        }

        public UserActionResult<Void> getResult() {
            return result;
        }

        public void setResult(UserActionResult<Void> result) {
            this.result = Objects.requireNonNull(result);
        }

        public void denyInteraction(Component reason) {
            this.setResult(UserActionResult.failure(reason));
        }
    }

    public static final class OnFinished extends EntityInteractionEvent {

        private final EntityInteraction interaction;
        private final EntityInteraction.InteractionResult interactionResult;

        public OnFinished(EntityInteraction.Type<?> interactionType, EntityInteraction.Context interactionContext, EntityInteraction interaction, EntityInteraction.InteractionResult interactionResult) {
            super(interactionType, interactionContext);
            this.interaction = interaction;
            this.interactionResult = interactionResult;
        }

        public EntityInteraction getInteraction() {
            return interaction;
        }

        public EntityInteraction.InteractionResult getInteractionResult() {
            return interactionResult;
        }
    }
}
