package tnt.tarkovcraft.core.client.notification;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.Notification;

public class ClientNotification {

    private final Notification notification;
    private int lifetime;

    public ClientNotification(Notification notification) {
        this.notification = notification;
        this.lifetime = notification.getLifetime();
    }

    public IValidationResult.Severity severity() {
        return this.notification.getSeverity();
    }

    public Component label() {
        return this.notification.getLabel();
    }

    public boolean tickNotification() {
        return this.lifetime-- <= 0;
    }
}
