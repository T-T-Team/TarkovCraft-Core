package tnt.tarkovcraft.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;

public class TarkovCraftCoreClientEventHandler {

    @SubscribeEvent
    public void handlePostClientTickEvent(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        // notification tick
        if (screen == null) {
            NotificationChannel.MAIN.update();
        }
    }

    @SubscribeEvent
    public void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        NotificationChannel.MAIN.clearAllNotifications();
    }
}
