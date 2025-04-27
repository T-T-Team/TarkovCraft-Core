package tnt.tarkovcraft.core.client.notification;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.Notification;

public class ClientNotification {

    private final Notification notification;
    private final Component label;
    private int lifetime;

    public ClientNotification(Notification notification) {
        this.notification = notification;
        this.lifetime = notification.getLifetime();
        this.label = notification.getLabel().copy().withStyle(notification.getSeverity());
    }

    public Notification.Severity severity() {
        return this.notification.getSeverity();
    }

    public Component label() {
        return this.label;
    }

    public ResourceLocation icon() {
        return this.notification.getIcon();
    }

    public boolean tickNotification() {
        return this.lifetime-- <= 0;
    }
}
