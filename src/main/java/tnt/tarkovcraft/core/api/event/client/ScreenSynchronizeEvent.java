package tnt.tarkovcraft.core.api.event.client;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;

public class ScreenSynchronizeEvent extends Event {

    private final Screen currentScreen;
    private final SynchronizableScreen.DataSource dataSource;

    public ScreenSynchronizeEvent(Screen currentScreen, SynchronizableScreen.DataSource dataSource) {
        this.currentScreen = currentScreen;
        this.dataSource = dataSource;
    }

    public Screen getScreen() {
        return currentScreen;
    }

    public SynchronizableScreen.DataSource getDataSource() {
        return dataSource;
    }
}
