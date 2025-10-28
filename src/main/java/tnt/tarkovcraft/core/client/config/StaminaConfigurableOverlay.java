package tnt.tarkovcraft.core.client.config;

import dev.toma.configuration.config.Configurable;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.VerticalAlignment;

public class StaminaConfigurableOverlay extends ConfigurableOverlay {

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    @Configurable.Comment(value = "Background color of stamina status bar", localize = true)
    public String backgroundColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    @Configurable.Comment(value = "Gradient start color of stamina status bar", localize = true)
    public String barGradientStartColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    @Configurable.Comment(value = "Gradient end color of stamina status bar", localize = true)
    public String barGradientEndColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    @Configurable.Comment(value = "Gradient start color of stamina status bar when reached critical treshold", localize = true)
    public String barGradientStartCriticalColor;

    @Configurable
    @Configurable.Gui.ColorValue(isARGB = true)
    @Configurable.Comment(value = "Gradient end color of stamina status bar when reached critical treshold", localize = true)
    public String barGradientEndCriticalColor;

    public StaminaConfigurableOverlay(boolean enabled, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, int x, int y, String backgroundColor, String barGradientStartColor, String barGradientEndColor, String barGradientStartCriticalColor, String barGradientEndCriticalColor) {
        super(enabled, horizontalAlignment, verticalAlignment, x, y);
        this.backgroundColor = backgroundColor;
        this.barGradientStartColor = barGradientStartColor;
        this.barGradientEndColor = barGradientEndColor;
        this.barGradientStartCriticalColor = barGradientStartCriticalColor;
        this.barGradientEndCriticalColor = barGradientEndCriticalColor;
    }
}
