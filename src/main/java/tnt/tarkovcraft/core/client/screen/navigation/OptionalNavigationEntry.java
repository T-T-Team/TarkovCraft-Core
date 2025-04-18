package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.function.Function;
import java.util.function.Predicate;

public record OptionalNavigationEntry(Component label, Predicate<Context> available, Function<Context, Screen> screenProvider, int order) implements NavigationEntry {

    @Override
    public Screen getScreen(Context context) {
        return this.screenProvider.apply(context);
    }

    @Override
    public boolean isAvailable(Context context) {
        return this.available.test(context);
    }
}
