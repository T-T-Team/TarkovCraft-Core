package tnt.tarkovcraft.core.client.notification;

import com.mojang.blaze3d.platform.Window;
import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.Deque;
import java.util.function.IntUnaryOperator;

public class NotificationLayer implements LayeredDraw.Layer {

    public static final ResourceLocation LAYER_ID = TarkovCraftCore.createResourceLocation("layer/notification");
    public static final int NOTIFICATION_Z_LAYER = 1000;
    public static final IntUnaryOperator DEFAULT_NOTIFICATION_WIDTH = w -> Mth.ceil(w / 2.5F);
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
        drawNotifications(guiGraphics, font, windowWidth, window.getGuiScaledHeight(), this.channel, DEFAULT_NOTIFICATION_WIDTH.applyAsInt(windowWidth));
    }

    public static void drawNotifications(GuiGraphics graphics, Font font, int windowWidth, int windowHeight, NotificationChannel channel, int maxWidth) {
        Deque<ClientNotification> notifications = channel.getNotifications();
        if (notifications.isEmpty()) {
            return;
        }
        int y = windowHeight - 11;
        int left = windowWidth - maxWidth;
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, NOTIFICATION_Z_LAYER);
        for (ClientNotification notification : notifications) {
            graphics.fill(left, y, windowWidth, y + 10, ColorPalette.BG_TRANSPARENT_NORMAL);
            IValidationResult.Severity severity = notification.severity();
            if (severity.isWarningOrError()) {
                graphics.blit(RenderType::guiTextured, severity.iconPath, left + 1, y + 1, 0.0F, 0.0F, 8, 8, 16, 16, 16, 16);
            }
            graphics.drawScrollingString(font, notification.label(), left + 12, windowWidth, y + 1, severity.textColor);
            y -= 11;
        }
        graphics.pose().popPose();
    }
}
