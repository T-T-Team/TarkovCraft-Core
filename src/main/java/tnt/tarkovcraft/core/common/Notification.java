package tnt.tarkovcraft.core.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.network.message.notification.S2C_SendNotification;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class Notification {

    public static final Marker MARKER = MarkerManager.getMarker("Notification");
    public static final int DEFAULT_LIFETIME = 100;
    public static final StreamCodec<RegistryFriendlyByteBuf, Notification> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Severity.class), Notification::getSeverity,
            ComponentSerialization.STREAM_CODEC, Notification::getLabel,
            ResourceLocation.STREAM_CODEC, Notification::getIcon,
            ByteBufCodecs.INT, Notification::getLifetime,
            Notification::new
    );

    private final Severity notificationSeverity;
    private final Component label;
    private int lifetime;
    private ResourceLocation customIcon;

    private Notification(Severity notificationSeverity, Component label, ResourceLocation icon, int lifetime) {
        this.notificationSeverity = Objects.requireNonNull(notificationSeverity);
        this.label = Objects.requireNonNull(label);
        this.customIcon = icon;
        this.lifetime = lifetime;
    }

    public static Notification of(Severity severity, Component label, int lifetime) {
        return new Notification(severity, label, null, lifetime);
    }

    public static Notification info(Component label) {
        return of(Severity.INFO, label, DEFAULT_LIFETIME);
    }

    public static Notification warn(Component label) {
        return of(Severity.WARNING, label, DEFAULT_LIFETIME);
    }

    public static Notification error(Component label) {
        return of(Severity.ERROR, label, DEFAULT_LIFETIME);
    }

    public static Notification system(Component label) {
        return of(Severity.SYSTEM, label, DEFAULT_LIFETIME);
    }

    public void send(ServerPlayer target) {
        TarkovCraftCore.LOGGER.debug(MARKER, "Sending {} notification to {} - {}", this.notificationSeverity, target.getName().getString(), this.label.getString());
        PacketDistributor.sendToPlayer(target, new S2C_SendNotification(this));
    }

    public Severity getSeverity() {
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

    public void setIcon(ResourceLocation customIcon) {
        this.customIcon = customIcon;
    }

    public ResourceLocation getIcon() {
        return this.customIcon != null ? this.customIcon : this.notificationSeverity.getIcon();
    }

    public enum Severity implements UnaryOperator<Style> {

        SYSTEM(style -> style.withBold(true).withItalic(true).applyFormat(ChatFormatting.GOLD)),
        INFO(style -> style.applyFormat(ChatFormatting.GREEN)),
        WARNING(style -> style.applyFormat(ChatFormatting.YELLOW)),
        ERROR(style -> style.applyFormat(ChatFormatting.RED)),;

        private final UnaryOperator<Style> labelStylization;
        private final ResourceLocation icon;

        Severity(UnaryOperator<Style> labelStylization) {
            this.labelStylization = labelStylization;
            this.icon = TarkovCraftCore.createResourceLocation("textures/icons/notification/" + this.name().toLowerCase() + ".png");
        }

        public ResourceLocation getIcon() {
            return icon;
        }

        @Override
        public Style apply(Style style) {
            return labelStylization.apply(style);
        }
    }
}
