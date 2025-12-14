package tnt.tarkovcraft.core.client.notification;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

import java.util.Deque;
import java.util.function.IntUnaryOperator;

public class NotificationLayer implements GuiLayer {

    public static final Identifier LAYER_ID = TarkovCraftCore.createIdentifier("layer/notification");
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
        graphics.nextStratum();
        for (ClientNotification notification : notifications) {
            graphics.fill(left, y, windowWidth, y + 10, ColorPalette.BG_TRANSPARENT_NORMAL);
            RenderUtils.blitFull(graphics, notification.icon(), left, y, left + 10, y + 10);
            RenderUtils.drawScrollingString(notification.label(), graphics.textRenderer(GuiGraphics.HoveredTextEffects.NONE), left + 12, left + 12 + windowWidth, y + 1, y + 10, 0);
            y -= 11;
        }
    }
}
