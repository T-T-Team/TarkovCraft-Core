package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class DynamicNavigationProvider implements NavigationProvider {

    private final List<NavigationEntry> entries;

    public DynamicNavigationProvider() {
        this.entries = new ArrayList<>();
    }

    public synchronized NavigationEntry register(NavigationEntry entry) {
        this.entries.add(entry);
        this.entries.sort(Comparator.comparingInt(NavigationEntry::order));
        return entry;
    }

    public synchronized NavigationEntry registerSimple(Component label, BiFunction<Screen, UUID, Screen> provider, int order) {
        return register(new SimpleNavigationEntry(label, provider, order));
    }

    public synchronized NavigationEntry registerSimple(Component label, BiFunction<Screen, UUID, Screen> provider) {
        return registerSimple(label, provider, Integer.MAX_VALUE);
    }

    public synchronized NavigationEntry registerOptional(Component label, BiPredicate<Screen, UUID> filter, BiFunction<Screen, UUID, Screen> provider, int order) {
        return register(new OptionalNavigationEntry(label, filter, provider, order));
    }

    public synchronized NavigationEntry registerOptional(Component label, BiPredicate<Screen, UUID> filter, BiFunction<Screen, UUID, Screen> provider) {
        return registerOptional(label, filter, provider, Integer.MAX_VALUE);
    }

    public synchronized Screen buildInitial(Screen screen, UUID userId) {
        for (NavigationEntry entry : this.entries) {
            if (entry.isAvailable(screen, userId)) {
                return entry.getScreen(screen, userId);
            }
        }
        return null;
    }

    @Override
    public List<NavigationEntry> getNavigationEntries() {
        return this.entries;
    }
}
