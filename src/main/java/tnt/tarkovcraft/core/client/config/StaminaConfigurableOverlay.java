package tnt.tarkovcraft.core.client.config;

import dev.toma.configuration.config.Configurable;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.VerticalAlignment;

public class StaminaConfigurableOverlay extends ConfigurableOverlay {

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    public String backgroundColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    public String barGradientStartColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    public String barGradientEndColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    public String barGradientStartCriticalColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    public String barGradientEndCriticalColor;

    public StaminaConfigurableOverlay(boolean enabled, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int x, int y, int width, int height, String backgroundColor, String barGradientStartColor, String barGradientEndColor, String barGradientStartCriticalColor, String barGradientEndCriticalColor) {
        super(enabled, horizontalAlignment, verticalAlignment, x, y);
        this.backgroundColor = backgroundColor;
        this.barGradientStartColor = barGradientStartColor;
        this.barGradientEndColor = barGradientEndColor;
        this.barGradientStartCriticalColor = barGradientStartCriticalColor;
        this.barGradientEndCriticalColor = barGradientEndCriticalColor;
    }
}
