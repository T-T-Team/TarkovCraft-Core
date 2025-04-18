package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.function.Function;

public record SimpleNavigationEntry(Component label, Function<Context, Screen> screenProvider, int order) implements NavigationEntry {

    @Override
    public Screen getScreen(Context context) {
        return this.screenProvider.apply(context);
    }

    @Override
    public boolean isAvailable(Context context) {
        return true;
    }
}
