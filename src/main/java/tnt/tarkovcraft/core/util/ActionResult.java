package tnt.tarkovcraft.core.util;

import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActionResult<R> {

    private final Callable<R> action;

    protected ActionResult(Callable<R> action) {
        this.action = action;
    }

    public static <R> ActionResult<R> withResult(Callable<R> action) {
        return new ActionResult<>(action);
    }

    public static <R> R withResultHandled(Callable<R> action, Function<ActionResult<R>, R> handler) {
        ActionResult<R> result = withResult(action);
        return handler.apply(result);
    }

    public <X extends Throwable> R orThrow(Function<Exception, X> exception) throws X {
        try {
            return this.action.call();
        } catch (Exception e) {
            throw exception.apply(e);
        }
    }

    public R orHandleError(R result, Consumer<Exception> handler) {
        try {
            return this.action.call();
        } catch (Exception e) {
            handler.accept(e);
            return result;
        }
    }

    public R orHandleError(Consumer<Exception> handler) {
        return this.orHandleError(null, handler);
    }

    public R orHandleError(R result, Logger logger) {
        return this.orHandleError(result, exc -> logger.error("Operation failed", exc));
    }

    public R orHandleError(Logger logger) {
        return this.orHandleError(null, logger);
    }
}
