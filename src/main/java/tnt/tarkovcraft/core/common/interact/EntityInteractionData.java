package tnt.tarkovcraft.core.common.interact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.api.event.CoreEventHooks;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.util.Codecs;
import tnt.tarkovcraft.core.util.UserActionResult;

import javax.annotation.Nullable;
import java.util.Optional;

public final class EntityInteractionData {

    public static final Marker MARKER = MarkerManager.getMarker("EntityInteraction");
    public static final long INTERACTION_TTL = 1000L;
    private @Nullable InteractionTracker activeInteraction;
    public static final Codec<EntityInteractionData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            InteractionTracker.CODEC.optionalFieldOf("active_interaction").forGetter(t -> Optional.ofNullable(t.activeInteraction))
    ).apply(instance, EntityInteractionData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityInteractionData> STREAM_CODEC = StreamCodec.composite(
            InteractionTracker.STREAM_CODEC.apply(ByteBufCodecs::optional), t -> Optional.ofNullable(t.activeInteraction),
            EntityInteractionData::new
    );

    private EntityInteractionData(Optional<InteractionTracker> activeInteraction) {
        this.activeInteraction = activeInteraction.orElse(null);
    }

    public static EntityInteractionData create() {
        return new EntityInteractionData(Optional.empty());
    }

    public static EntityInteractionData getInteractionData(LivingEntity entity) {
        return entity.getData(CoreDataAttachments.INTERACTION_DATA);
    }

    public boolean isAnyInteractionActive() {
        return this.activeInteraction != null;
    }

    public boolean isInteractionActive(EntityInteraction.Type<?> interaction) {
        return this.activeInteraction != null && this.activeInteraction.interaction().type() == interaction;
    }

    public EntityInteraction getActiveInteraction() {
        return this.activeInteraction == null ? null : this.activeInteraction.interaction();
    }

    public boolean isInteractionReady(long gameTime) {
        if (this.activeInteraction == null)
            return false;
        return this.activeInteraction.isFinished(gameTime);
    }

    public boolean isInteractionExpired(long gameTime) {
        if (this.activeInteraction == null)
            return false;
        return this.activeInteraction.isExpired(gameTime);
    }

    public <T extends EntityInteraction> boolean startInteraction(EntityInteraction.Type<T> interaction, EntityInteraction.Context context, long initiationTime) {
        if (this.isAnyInteractionActive()) {
            TarkovCraftCore.LOGGER.warn(MARKER, "Skipping new interaction creation, an interaction is already active: {}", this.activeInteraction);
            return false;
        }
        UserActionResult<Void> initiateResult = interaction.canUseInteraction(context);
        if (initiateResult.isFailure()) {
            TarkovCraftCore.LOGGER.debug(MARKER, "Interaction {} failed for player {} due to reason: {}", this.activeInteraction, context.player(), initiateResult.message().getString());
            return false;
        }
        EntityInteraction instance = interaction.instantiate(context);
        this.activeInteraction = InteractionTracker.create(initiationTime, instance);
        TarkovCraftCore.LOGGER.debug(MARKER, "Interaction {} started by player {} for {} with duration of {} ticks. Target timestamp: {}", this.activeInteraction, context.player(), context.target(), this.activeInteraction.interactionDuration, this.activeInteraction.targetTimestamp());
        return true;
    }

    public EntityInteraction.InteractionResult finishInteraction(EntityInteraction.Context context) {
        return this.finishInteraction(context, false);
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityInteraction> EntityInteraction.InteractionResult finishInteraction(EntityInteraction.Context context, boolean ignoreDuration) {
        if (!this.isAnyInteractionActive()) {
            TarkovCraftCore.LOGGER.warn(MARKER, "Failed to finish active interaction as there is no interaction active");
            return EntityInteraction.InteractionResult.EXPIRED;
        }
        long serverTime = context.player().level().getGameTime();
        if (this.activeInteraction.isFinished(serverTime) || ignoreDuration) {
            TarkovCraftCore.LOGGER.debug(MARKER, "Finishing interaction {} by player {} for {}", this.activeInteraction, context.player(), context.target());
            T interaction = (T) this.activeInteraction.interaction();
            EntityInteraction.Type<T> interactionType = (EntityInteraction.Type<T>) interaction.type();
            UserActionResult<Void> preconditionResult = interactionType.canUseInteraction(context);
            EntityInteraction.InteractionResult result;
            if (preconditionResult.isSuccess()) {
                result = EntityInteraction.InteractionResult.SUCCESS;
                interaction.onCompleted(context);
            } else {
                result = EntityInteraction.InteractionResult.FAILURE;
                interaction.onFailed(context, result);
            }
            CoreEventHooks.onInteractionFinished(interaction, context, result);
            this.activeInteraction = null;
            return result;
        }
        TarkovCraftCore.LOGGER.warn(MARKER, "Interaction {} is not finished yet, skipping finishing. Current timestamp: {}, target timestamp: {}", this.activeInteraction, serverTime, this.activeInteraction.targetTimestamp());
        return EntityInteraction.InteractionResult.PENDING;
    }

    public EntityInteraction.InteractionResult cancelInteraction(EntityInteraction.Context context, EntityInteraction.InteractionResult requestedState) {
        TarkovCraftCore.LOGGER.debug(MARKER, "Cancelling interaction {} by player {} for {}", this.activeInteraction, context.player(), context.target());
        if (this.isAnyInteractionActive()) {
            TarkovCraftCore.LOGGER.debug(MARKER, "Found active cancellable interaction {} by player {} for {}", this.activeInteraction, context.player(), context.target());
            this.activeInteraction.interaction.onFailed(context, requestedState);
            CoreEventHooks.onInteractionFinished(this.activeInteraction.interaction, context, requestedState);
            this.activeInteraction = null;
            return EntityInteraction.InteractionResult.CANCELLED;
        }
        return EntityInteraction.InteractionResult.EXPIRED;
    }

    public void sync(LivingEntity entity) {
        if (entity.level().isClientSide())
            return;
        entity.syncData(CoreDataAttachments.INTERACTION_DATA);
    }

    private record InteractionTracker(EntityInteraction interaction, int interactionDuration, long interactionStartedAt) {

        static final Codec<InteractionTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityInteraction.CODEC.fieldOf("interaction").forGetter(InteractionTracker::interaction),
                Codec.INT.optionalFieldOf("duration", 0).forGetter(InteractionTracker::interactionDuration),
                Codec.LONG.optionalFieldOf("initiatedAt", 0L).forGetter(InteractionTracker::interactionStartedAt)
        ).apply(instance, InteractionTracker::new));
        static final StreamCodec<RegistryFriendlyByteBuf, InteractionTracker> STREAM_CODEC = StreamCodec.composite(
                EntityInteraction.STREAM_CODEC, InteractionTracker::interaction,
                ByteBufCodecs.INT, InteractionTracker::interactionDuration,
                Codecs.LONG_STREAM_CODEC, InteractionTracker::interactionStartedAt,
                InteractionTracker::new
        );

        static InteractionTracker create(long initiationTime, EntityInteraction interaction) {
            int duration = interaction.type().duration();
            return new InteractionTracker(interaction, duration, initiationTime);
        }

        private boolean isFinished(long currentTime) {
            long remainingDuration = this.getRemainingDuration(currentTime);
            return remainingDuration <= 0 && Math.abs(remainingDuration) < INTERACTION_TTL;
        }

        private boolean isExpired(long currentTime) {
            long remainingDuration = this.getRemainingDuration(currentTime);
            return Math.abs(remainingDuration) >= INTERACTION_TTL;
        }

        private int getRemainingDuration(long currentTime) {
            long diff = currentTime - this.interactionStartedAt;
            return (int) (this.interactionDuration - diff);
        }

        private long targetTimestamp() {
            return this.interactionStartedAt + this.interactionDuration;
        }
    }
}
