package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.UUID;

public interface NavigationEntry {

    Component label();

    boolean isAvailable(Screen parent, UUID userId);

    Screen getScreen(Screen parent, UUID userId);

    int order();
}
