package tnt.tarkovcraft.core.common;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.network.message.notification.S2C_SendNotification;

import java.util.Objects;

public final class Notification {

    public static final Marker MARKER = MarkerManager.getMarker("Notification");
    public static final int DEFAULT_LIFETIME = 100;
    public static final StreamCodec<RegistryFriendlyByteBuf, Notification> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(IValidationResult.Severity.class), Notification::getSeverity,
            ComponentSerialization.STREAM_CODEC, Notification::getLabel,
            ByteBufCodecs.INT, Notification::getLifetime,
            Notification::new
    );

    private final IValidationResult.Severity notificationSeverity;
    private final Component label;
    private int lifetime;

    private Notification(IValidationResult.Severity notificationSeverity, Component label, int lifetime) {
        this.notificationSeverity = Objects.requireNonNull(notificationSeverity);
        this.label = Objects.requireNonNull(label);
        this.lifetime = lifetime;
    }

    public static Notification of(IValidationResult.Severity severity, Component label, int lifetime) {
        return new Notification(severity, label, lifetime);
    }

    public static Notification info(Component label) {
        return of(IValidationResult.Severity.NONE, label, DEFAULT_LIFETIME);
    }

    public static Notification warn(Component label) {
        return of(IValidationResult.Severity.WARNING, label, DEFAULT_LIFETIME);
    }

    public static Notification error(Component label) {
        return of(IValidationResult.Severity.ERROR, label, DEFAULT_LIFETIME);
    }

    public void send(ServerPlayer target) {
        TarkovCraftCore.LOGGER.debug(MARKER, "Sending {} notification to {} - {}", this.notificationSeverity, target.getName().getString(), this.label.getString());
        PacketDistributor.sendToPlayer(target, new S2C_SendNotification(this));
    }

    public IValidationResult.Severity getSeverity() {
        return this.notificationSeverity;
    }

    public Component getLabel() {
        return this.label;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    public int getLifetime() {
        return lifetime;
    }
}
