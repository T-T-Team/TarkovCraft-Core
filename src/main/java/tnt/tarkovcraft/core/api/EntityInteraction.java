package tnt.tarkovcraft.core.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.event.CoreEventHooks;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.interact.EntityInteractionData;
import tnt.tarkovcraft.core.util.UserActionResult;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface EntityInteraction {

    Codec<EntityInteraction> CODEC = CoreRegistries.ENTITY_INTERACTION.byNameCodec().dispatch(EntityInteraction::type, Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, EntityInteraction> STREAM_CODEC = ByteBufCodecs.registry(CoreRegistries.Keys.ENTITY_INTERACTION).dispatch(EntityInteraction::type, Type::streamCodec);
    String LOCALIZATION_PREFIX = "entity_interaction";
    Identifier SHARED_ERROR_IDENTIFIER = TarkovCraftCore.createIdentifier("shared");
    UserActionResult<Void> ENTITY_TOO_FAR = UserActionResult.failure(Type.getErrorMessage(SHARED_ERROR_IDENTIFIER, "entity_too_far"));
    UserActionResult<Void> ANOTHER_INTERACTION_ACTIVE = UserActionResult.failure(Type.getErrorMessage(SHARED_ERROR_IDENTIFIER, "another_interaction_active"));
    UserActionResult<Void> INTERACTION_CANCELLED = UserActionResult.failure(Type.getErrorMessage(SHARED_ERROR_IDENTIFIER, "cancelled"));
    int MAX_DISTANCE_SQR = 16;

    void onStarted(Context context);

    void onCompleted(Context context);

    void onFailed(Context context, InteractionResult reason);

    Type<?> type();

    default Component getDisplayName() {
        return this.type().displayName();
    }

    record Context(Player player, LivingEntity target, ItemStack itemStack) {

        public Context(Player player, LivingEntity target) {
            this(player, target, player.getMainHandItem());
        }

        public boolean isSelfInteraction() {
            return this.player == this.target;
        }

        public double getInteractionDistanceSqr() {
            return this.isSelfInteraction() ? 0.0D : this.player.distanceToSqr(this.target);
        }

        public static Context self(Player player) {
            return new Context(player, player, player.getMainHandItem());
        }
    }

    final class Type<T extends EntityInteraction> {

        private final Identifier identifier;
        private final MapCodec<T> codec;
        private final StreamCodec<? super ByteBuf, T> streamCodec;
        private final InteractionFactory<T> factory;
        private final InteractionPredicate predicate;
        private final int maxRangeSqr;
        private final int duration;
        private final Component displayName;

        private Type(Builder<T> builder) {
            this.identifier = builder.identifier;
            this.codec = builder.codec;
            this.streamCodec = builder.streamCodec;
            this.factory = builder.factory;
            this.predicate = builder.predicate;
            this.maxRangeSqr = builder.maxRangeSqr;
            this.duration = builder.duration;
            this.displayName = builder.displayName;
        }

        public static <T extends EntityInteraction> Builder<T> builder(Identifier identifier) {
            return new Builder<>(Objects.requireNonNull(identifier, "Identifier cannot be null"));
        }

        public static <T extends EntityInteraction> Builder<T> singletonBuilder(Identifier identifier, T interactionInstance) {
            return Type.<T>builder(identifier)
                    .withFactory(_ -> interactionInstance);
        }

        public static List<EntityInteraction.Type<?>> listAvailableInteractions(Context context) {
            LivingEntity target = context.target();
            EntityInteractionData interactionData = EntityInteractionData.getInteractionData(target);
            if (interactionData.isInteractionAllowed(context)) {
                return CoreRegistries.ENTITY_INTERACTION.stream()
                        .filter(t -> t.canUseInteraction(context).isSuccess())
                        .toList();
            }
            return Collections.emptyList();
        }

        public T instantiate(Context context) {
            return this.factory.createInstance(context);
        }

        public UserActionResult<Void> canUseInteraction(Context context) {
            EntityInteractionData interactionData = EntityInteractionData.getInteractionData(context.target());
            if (!interactionData.isInteractionAllowed(context)) {
                return ANOTHER_INTERACTION_ACTIVE;
            }
            double distance = context.getInteractionDistanceSqr();
            if (distance > this.maxRangeSqr) {
                return ENTITY_TOO_FAR;
            }
            UserActionResult<Void> result = this.predicate.checkAvailability(context);
            return CoreEventHooks.checkInteractionAvailability(this, context, result);
        }

        public MapCodec<T> codec() {
            return this.codec;
        }

        public StreamCodec<? super ByteBuf, T> streamCodec() {
            return this.streamCodec;
        }

        public int duration() {
            return this.duration;
        }

        public Component displayName() {
            return this.displayName;
        }

        public static Component getErrorMessage(Identifier identifier, String errorCode, Object... args) {
            String key = identifier.toLanguageKey(LOCALIZATION_PREFIX, "error." + errorCode);
            return Component.translatable(key, args);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Type<?> type)) return false;
            return Objects.equals(identifier, type.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(identifier);
        }

        @Override
        public String toString() {
            return this.identifier.toString();
        }
    }

    @FunctionalInterface
    interface InteractionFactory<T extends EntityInteraction> {
        T createInstance(Context context);
    }

    @FunctionalInterface
    interface InteractionPredicate {
        InteractionPredicate ALWAYS_ALLOWED = _ -> UserActionResult.successEmpty();

        UserActionResult<Void> checkAvailability(Context context);
    }

    enum InteractionResult {

        SUCCESS,
        FAILURE,
        EXPIRED,
        PENDING,
        CANCELLED;

        public boolean isSuccess() {
            return this == SUCCESS;
        }

        public boolean isFailure() {
            return this == FAILURE;
        }

        public boolean isSuccessOrFailure() {
            return this.isSuccess() || this.isFailure();
        }
    }

    final class Builder<T extends EntityInteraction> {

        private final Identifier identifier;
        private MapCodec<T> codec;
        private StreamCodec<? super ByteBuf, T> streamCodec;
        private InteractionFactory<T> factory;
        private InteractionPredicate predicate = InteractionPredicate.ALWAYS_ALLOWED;
        private int maxRangeSqr = MAX_DISTANCE_SQR;
        private int duration = 100;
        private Component displayName;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
            this.displayName = Component.translatable(identifier.toLanguageKey(LOCALIZATION_PREFIX));
        }

        public Builder<T> withSerializer(MapCodec<T> codec, StreamCodec<? super ByteBuf, T> streamCodec) {
            this.codec = codec;
            this.streamCodec = streamCodec;
            return this;
        }

        public Builder<T> withFactory(InteractionFactory<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> withPredicate(InteractionPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder<T> withMaxRange(int maxRangeSqr) {
            this.maxRangeSqr = maxRangeSqr;
            return this;
        }

        public Builder<T> withDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder<T> withCustomDisplayName(Component displayName) {
            this.displayName = displayName;
            return this;
        }

        public Type<T> build() {
            Objects.requireNonNull(this.codec, "Codec cannot be null");
            Objects.requireNonNull(this.streamCodec, "StreamCodec cannot be null");
            Objects.requireNonNull(this.factory, "Instance factory cannot be null");
            Objects.requireNonNull(this.predicate, "Predicate cannot be null");
            Objects.requireNonNull(this.displayName, "Display name cannot be null");
            return new Type<>(this);
        }
    }
}
