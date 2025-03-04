package tnt.tarkovcraft.core;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tnt.tarkovcraft.core.common.TarkovCraftCoreConfig;

@Mod(TarkovCraftCore.MOD_ID)
public class TarkovCraftCore {

    public static final String MOD_ID = "tarkovcraft_core";
    public static final Logger LOGGER = LogManager.getLogger("TarkovCraftCore");

    private static TarkovCraftCoreConfig config;

    public TarkovCraftCore(IEventBus modEventBus, ModContainer container) {
        config = Configuration.registerConfig(TarkovCraftCoreConfig.class, ConfigFormats.YAML).getConfigInstance();
    }

    public static TarkovCraftCoreConfig getConfig() {
        return config;
    }
}
