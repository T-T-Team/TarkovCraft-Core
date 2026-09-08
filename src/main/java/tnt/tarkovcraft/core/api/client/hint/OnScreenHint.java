package tnt.tarkovcraft.core.api.client.hint;

import net.minecraft.client.Minecraft;
import tnt.tarkovcraft.core.client.config.OnScreenHintDisplay;

public interface OnScreenHint {

    OnScreenHintRenderer<?> createRenderer();

    void tick(Minecraft client);

    void setActive(boolean active);

    void setVisible(boolean visible);

    boolean isActive();

    boolean isVisible();

    boolean isAdvancedHint();

    boolean shouldRender(OnScreenHintDisplay displayMode);
}
