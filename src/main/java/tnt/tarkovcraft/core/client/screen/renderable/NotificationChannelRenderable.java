package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;
import tnt.tarkovcraft.core.client.notification.NotificationLayer;

import java.util.function.IntUnaryOperator;

public class NotificationChannelRenderable implements Renderable {

    private final Font font;
    private final int windowWidth;
    private final int windowHeight;
    private final NotificationChannel channel;
    private IntUnaryOperator maxWidth = NotificationLayer.DEFAULT_NOTIFICATION_WIDTH;

    public NotificationChannelRenderable(Font font, int windowWidth, int windowHeight, NotificationChannel channel) {
        this.font = font;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.channel = channel;
    }

    public void setMaxWidth(IntUnaryOperator maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        NotificationLayer.drawNotifications(guiGraphics, this.font, this.windowWidth, this.windowHeight, this.getChannel(), this.maxWidth.applyAsInt(this.windowWidth));
    }

    public NotificationChannel getChannel() {
        return channel;
    }
}
