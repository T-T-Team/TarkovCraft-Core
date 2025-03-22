package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;

public abstract class NotificationScreen extends Screen {

    protected NotificationChannelRenderable notifications;

    public NotificationScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        this.notifications = this.createNotificationWidget();
        if (this.notifications != null) {
            this.addRenderableOnly(this.notifications);
        }
    }

    @Override
    public void tick() {
        if (this.notifications != null) {
            this.notifications.getChannel().update();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected NotificationChannelRenderable createNotificationWidget() {
        return new NotificationChannelRenderable(this.font, this.width, this.height, NotificationChannel.MAIN);
    }
}
