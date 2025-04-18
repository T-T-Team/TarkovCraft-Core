package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.context.Context;

public interface NavigationEntry {

    Component label();

    boolean isAvailable(Context context);

    Screen getScreen(Context context);

    int order();
}
