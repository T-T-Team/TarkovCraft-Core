package tnt.tarkovcraft.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Deprecated
public class Action extends ActionResult<Void> {

    protected Action(ActionRunnable runnable) {
        super(() -> {
            runnable.run();
            return null;
        });
    }

    public static Action none() {
        return prepare(() -> {});
    }

    public static <X extends Exception> Action error(Supplier<X> error) throws X {
        return prepare(() -> {
            throw error.get();
        });
    }

    public static Action prepare(ActionRunnable runnable) {
        return new Action(runnable);
    }

    public static void run(ActionRunnable runnable, Consumer<Action> handler) {
        Action action = prepare(runnable);
        handler.accept(action);
    }

    @FunctionalInterface
    public interface ActionRunnable {
        void run() throws Exception;
    }
}
