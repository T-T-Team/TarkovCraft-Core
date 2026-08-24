package tnt.tarkovcraft.core.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.interact.EntityInteractionData;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.function.IntFunction;

public record C2S_RequestInteractionState(EntityInteraction.Type<?> interactionType, State state, int entityId, long clientTimeStamp) implements CustomPacketPayload {

    public static final ResourceLocation PACKET_ID = TarkovCraftCoreNetwork.createId(C2S_RequestInteractionState.class);
    public static final Type<C2S_RequestInteractionState> TYPE = new Type<>(PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, C2S_RequestInteractionState> CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(CoreRegistries.Keys.ENTITY_INTERACTION), C2S_RequestInteractionState::interactionType,
            State.STREAM_CODEC, C2S_RequestInteractionState::state,
            ByteBufCodecs.INT, C2S_RequestInteractionState::entityId,
            Codecs.LONG_STREAM_CODEC, C2S_RequestInteractionState::clientTimeStamp,
            C2S_RequestInteractionState::new
    );
    private static final long TIME_DIFF_TOLERANCE = 30L; // 1.5s for possible network delays etc

    public static C2S_RequestInteractionState start(EntityInteraction.Type<?> interactionType, LivingEntity entity, long clientTimeStamp) {
        return new C2S_RequestInteractionState(interactionType, State.START, entity.getId(), clientTimeStamp);
    }

    public static C2S_RequestInteractionState finish(EntityInteraction.Type<?> interactionType, LivingEntity entity) {
        return new C2S_RequestInteractionState(interactionType, State.FINISH, entity.getId(), 0L);
    }

    public static C2S_RequestInteractionState cancel(EntityInteraction.Type<?> interactionType, LivingEntity entity) {
        return new C2S_RequestInteractionState(interactionType, State.CANCEL, entity.getId(), 0L);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handleMessage(IPayloadContext context) {
        Player player = context.player();
        Level level = player.level();
        Entity entity = level.getEntity(this.entityId);
        if (!(entity instanceof LivingEntity livingEntity))
            return;
        EntityInteractionData interactionData = EntityInteractionData.getInteractionData(player);
        TarkovCraftCore.LOGGER.debug(EntityInteractionData.MARKER, "Received interaction {} state for {}: {}", this.interactionType, livingEntity, this.state);
        long serverTimeStamp = level.getGameTime();
        // Interaction start
        EntityInteraction.Context interactionContext = new EntityInteraction.Context(player, livingEntity);
        if (this.state == State.START) {
            if (interactionData.isAnyInteractionActive()) {
                TarkovCraftCore.LOGGER.debug(EntityInteractionData.MARKER, "Received interaction {} state for {} while another interaction is active", this.interactionType, livingEntity);
                return;
            }
            if (Math.abs(serverTimeStamp - this.clientTimeStamp) > TIME_DIFF_TOLERANCE) {
                TarkovCraftCore.LOGGER.debug(EntityInteractionData.MARKER, "Received outdated interaction state for {}. Expected: {}, Actual: {}", livingEntity, this.clientTimeStamp, serverTimeStamp);
                return;
            }
            if (!interactionData.startInteraction(this.interactionType, interactionContext, this.clientTimeStamp)) {
                TarkovCraftCore.LOGGER.debug(EntityInteractionData.MARKER, "Failed to start interaction {} for {}", this.interactionType, livingEntity);
            }
        } else if (interactionData.isInteractionActive(this.interactionType)) {
            // Interaction completion - check if interaction has not expired first
            if (interactionData.isInteractionExpired(serverTimeStamp) || this.state == State.CANCEL) {
                EntityInteraction.InteractionResult requestState = this.state == State.CANCEL ? EntityInteraction.InteractionResult.CANCELLED : EntityInteraction.InteractionResult.EXPIRED;
                EntityInteraction.InteractionResult cancellationResult = interactionData.cancelInteraction(interactionContext, requestState);
                if (cancellationResult != requestState) {
                    TarkovCraftCore.LOGGER.debug(EntityInteractionData.MARKER, "Failed to cancel interaction {} for {} with state: {} Got {}", this.interactionType, livingEntity, requestState, cancellationResult);
                }
            } else {
                EntityInteraction.InteractionResult finishResult = interactionData.finishInteraction(interactionContext);
                if (!finishResult.isSuccessOrFailure()) {
                    TarkovCraftCore.LOGGER.debug(EntityInteractionData.MARKER, "Failed to finish interaction {} for {}", this.interactionType, livingEntity);
                }
            }
        }
        interactionData.sync(player);
    }

    public enum State {

        START,
        FINISH,
        CANCEL;

        private static final IntFunction<State> BY_ID = ByIdMap.continuous(State::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
        public static final StreamCodec<ByteBuf, State> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
    }
}
