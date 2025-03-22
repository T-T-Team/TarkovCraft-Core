package tnt.tarkovcraft.core.client.notification;

import com.mojang.blaze3d.platform.Window;
import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.Deque;
import java.util.List;

public class NotificationLayer implements LayeredDraw.Layer {

    public static final ResourceLocation LAYER_ID = TarkovCraftCore.createResourceLocation("layer/notification");
    private final NotificationChannel channel;

    public NotificationLayer(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        Font font = minecraft.font;
        int windowWidth = window.getGuiScaledWidth();
        drawNotifications(guiGraphics, font, windowWidth, window.getGuiScaledHeight(), this.channel, windowWidth / 3);
    }

    public static void drawNotifications(GuiGraphics graphics, Font font, int windowWith, int windowHeight, NotificationChannel channel, int maxWidth) {
        Deque<ClientNotification> notifications = channel.getNotifications();
        if (notifications.isEmpty()) {
            return;
        }
        int y = windowHeight - 10;
        int maxTextWidth = maxWidth - 12;
        int left = windowWith - maxWidth;
        for (ClientNotification notification : notifications) {
            graphics.fill(left, y, windowWith, y + 10, ColorPalette.BG_TRANSPARENT_NORMAL);
            IValidationResult.Severity severity = notification.severity();
            if (severity.isWarningOrError()) {
                // TODO icon render
            }

            List<FormattedCharSequence> split = font.split(notification.label(), maxTextWidth);
            if (!split.isEmpty()) {
                FormattedCharSequence text = split.getFirst();
                graphics.drawString(font, text, left + 12, y + 1, severity.textColor, false);
            }
            y -= 10;
        }
    }
}
