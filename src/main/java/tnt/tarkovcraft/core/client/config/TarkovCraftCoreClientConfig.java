package tnt.tarkovcraft.core.client.config;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.FieldVisibility;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.VerticalAlignment;

@Config(id = TarkovCraftCore.MOD_ID + "-client", filename = "tarkovCraftCore-client", group = TarkovCraftCore.MOD_ID)
public class TarkovCraftCoreClientConfig {

    @Configurable
    @Configurable.Gui.Visibility(FieldVisibility.ADVANCED)
    public boolean renderDebugOverlay = false;

    @Configurable
    public StaminaConfigurableOverlay moveStaminaOverlay = new StaminaConfigurableOverlay(true, HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, 5, -5, 80, 4, "#FF000000", "#FF00CE14", "#FF007F0E", "#FFC10000", "#FF780000");

    @Configurable
    public StaminaConfigurableOverlay armStaminaOverlay = new StaminaConfigurableOverlay(true, HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, 5, -9, 80, 4, "#FF000000", "#FF00B2C2", "#FF004EC2", "#FFC10000", "#FF780000");
}
