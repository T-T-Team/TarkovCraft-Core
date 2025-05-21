package tnt.tarkovcraft.core.compatibility;

import net.neoforged.fml.ModList;

import java.util.concurrent.Callable;

public final class ModIntegrationHelper {

    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static void run(String modId, Runnable action) {
        if (isLoaded(modId)) {
            action.run();
        }
    }

    public static <T> T call(String modId, Callable<T> action) {
        return call(modId, action, null);
    }

    public static <T> T call(String modId, Callable<T> action, T defaultValue) {
        if (isLoaded(modId)) {
            try {
                return action.call();
            } catch (Exception e) {
                throw new RuntimeException("Failed to execute mod specific action for mod " + modId, e);
            }
        }
        return defaultValue;
    }
}
