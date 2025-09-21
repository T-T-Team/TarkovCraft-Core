package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.UUID;
import java.util.function.BiFunction;

public record SimpleNavigationEntry(Component label, BiFunction<Screen, UUID, Screen> screenProvider, int order) implements NavigationEntry {

    @Override
    public Screen getScreen(Screen parent, UUID userId) {
        return this.screenProvider.apply(parent, userId);
    }

    @Override
    public boolean isAvailable(Screen parent, UUID userId) {
        return true;
    }
}
