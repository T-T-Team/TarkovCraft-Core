package tnt.tarkovcraft.core.client.config;

import tnt.tarkovcraft.core.client.hint.OnScreenHint;

import java.util.function.Predicate;

public enum OnScreenHintDisplay implements Predicate<OnScreenHint> {

    NONE(hint -> false),
    ADVANCED(OnScreenHint::isAdvanced),
    ALL(hint -> true);

    private final Predicate<OnScreenHint> predicate;

    OnScreenHintDisplay(Predicate<OnScreenHint> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(OnScreenHint onScreenHint) {
        return this.predicate.test(onScreenHint);
    }
}
