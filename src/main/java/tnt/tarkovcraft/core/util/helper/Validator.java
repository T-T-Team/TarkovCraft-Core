package tnt.tarkovcraft.core.util.helper;

import org.apache.commons.lang3.StringUtils;
import tnt.tarkovcraft.core.server.packs.resources.StackCollection;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public final class Validator {

    private static final Validator DEFAULT_VALIDATOR = create(IllegalArgumentException::new);
    private final Function<String, RuntimeException> exceptionFactory;

    private Validator(Function<String, RuntimeException> exceptionFactory) {
        this.exceptionFactory = exceptionFactory;
    }

    public static Validator getDefault() {
        return DEFAULT_VALIDATOR;
    }

    public static Validator create(Function<String, RuntimeException> exceptionFactory) {
        return new Validator(exceptionFactory);
    }

    public void requireNonNull(Object object, String fieldName) {
        if (object == null) {
            throw this.exceptionFactory.apply(fieldName + " must be defined");
        }
    }

    public void requireNonBlank(String text, String fieldName) {
        if (StringUtils.isBlank(text)) {
            throw this.exceptionFactory.apply(fieldName + " must not be empty text");
        }
    }

    public void requireNonEmpty(Object[] array, String fieldName) {
        if (array == null || array.length < 1) {
            throw this.exceptionFactory.apply(fieldName + " must contain at least one element");
        }
    }

    public void requireNonEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw this.exceptionFactory.apply(fieldName + " must contain at least one element");
        }
    }

    public void requireNonEmpty(Map<?, ?> map, String fieldName) {
        if (map == null || map.isEmpty()) {
            throw this.exceptionFactory.apply(fieldName + " must contain at least one element");
        }
    }

    public void requireNonEmpty(StackCollection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            throw this.exceptionFactory.apply(fieldName + " must contain at least one element");
        }
    }
}
