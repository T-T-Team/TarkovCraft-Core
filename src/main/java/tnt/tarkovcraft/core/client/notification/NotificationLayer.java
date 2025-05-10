package tnt.tarkovcraft.core.client.notification;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

import java.util.Deque;
import java.util.function.IntUnaryOperator;

public class NotificationLayer implements LayeredDraw.Layer {

    public static final ResourceLocation LAYER_ID = TarkovCraftCore.createResourceLocation("layer/notification");
    public static final int NOTIFICATION_Z_LAYER = 1000;
    public static final IntUnaryOperator DEFAULT_NOTIFICATION_WIDTH = w -> Mth.ceil(w * 0.45F);
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
            RenderUtils.blitFull(graphics, notification.icon(), left, y, left + 10, y + 10);
            graphics.drawScrollingString(font, notification.label(), left + 12, windowWidth, y + 1, 0xFFFFFF);
            y -= 11;
        }
        graphics.pose().popPose();
    }
}
