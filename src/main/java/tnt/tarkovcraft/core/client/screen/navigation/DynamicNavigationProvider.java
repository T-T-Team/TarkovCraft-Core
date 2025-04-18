package tnt.tarkovcraft.core.client.screen.navigation;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public synchronized NavigationEntry registerSimple(Component label, Function<Context, Screen> provider, int order) {
        return register(new SimpleNavigationEntry(label, provider, order));
    }

    public synchronized NavigationEntry registerSimple(Component label, Function<Context, Screen> provider) {
        return registerSimple(label, provider, Integer.MAX_VALUE);
    }

    public synchronized NavigationEntry registerOptional(Component label, Predicate<Context> context, Function<Context, Screen> provider, int order) {
        return register(new OptionalNavigationEntry(label, context, provider, order));
    }

    public synchronized NavigationEntry registerOptional(Component label, Predicate<Context> context, Function<Context, Screen> provider) {
        return registerOptional(label, context, provider, Integer.MAX_VALUE);
    }

    public synchronized Screen buildInitial(Context context) {
        for (NavigationEntry entry : this.entries) {
            if (entry.isAvailable(context)) {
                return entry.getScreen(context);
            }
        }
        return null;
    }

    @Override
    public List<NavigationEntry> getNavigationEntries() {
        return this.entries;
    }
}
