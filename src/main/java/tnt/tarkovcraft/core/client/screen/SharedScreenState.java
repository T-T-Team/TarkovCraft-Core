package tnt.tarkovcraft.core.client.screen;

import java.util.Objects;

public final class SharedScreenState<V> {

    private Object stateParent;
    private V stateValue;

    public void setState(Object parent, V value) {
        this.stateParent = parent;
        this.stateValue = value;
    }

    public void clearState(Object parent) {
        if (Objects.equals(this.stateParent, parent)) {
            this.stateParent = null;
            this.stateValue = null;
        }
    }

    public V getState() {
        return this.stateValue;
    }
}
