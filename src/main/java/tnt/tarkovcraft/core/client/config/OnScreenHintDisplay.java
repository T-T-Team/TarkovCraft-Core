package tnt.tarkovcraft.core.client.config;

import tnt.tarkovcraft.core.client.hint.AbstractOnScreenHint;

import java.util.function.Predicate;

public enum OnScreenHintDisplay implements Predicate<AbstractOnScreenHint> {

    NONE(t -> false),
    ADVANCED(AbstractOnScreenHint::isAdvancedHint),
    ALL(t -> true);

    private final Predicate<AbstractOnScreenHint> predicate;

    OnScreenHintDisplay(Predicate<AbstractOnScreenHint> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(AbstractOnScreenHint onScreenHint) {
        return this.predicate.test(onScreenHint);
    }
}
