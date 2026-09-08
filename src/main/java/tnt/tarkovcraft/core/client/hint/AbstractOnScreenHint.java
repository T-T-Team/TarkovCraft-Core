package tnt.tarkovcraft.core.client.hint;

import net.minecraft.client.Minecraft;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHint;
import tnt.tarkovcraft.core.client.config.OnScreenHintDisplay;

public abstract class AbstractOnScreenHint implements OnScreenHint {

    private boolean active;
    private boolean visible;

    public AbstractOnScreenHint() {
        this(true, true);
    }

    public AbstractOnScreenHint(boolean active, boolean visible) {
        this.active = active;
        this.visible = visible;
    }

    @Override
    public void tick(Minecraft client) {
    }

    public boolean isAdvancedHint() {
        return false;
    }

    public final void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public final boolean shouldRender(OnScreenHintDisplay displayMode) {
        return this.isActive() && this.isVisible() && displayMode.test(this);
    }
}
