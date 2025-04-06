package tnt.tarkovcraft.core.client;

import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.FieldVisibility;
import tnt.tarkovcraft.core.TarkovCraftCore;

@Config(id = TarkovCraftCore.MOD_ID + "-client", filename = "tarkovCraftCore-client", group = TarkovCraftCore.MOD_ID)
public class TarkovCraftCoreClientConfig {

    @Configurable
    @Configurable.Gui.Visibility(FieldVisibility.ADVANCED)
    public boolean renderDebugOverlay = false;
}
