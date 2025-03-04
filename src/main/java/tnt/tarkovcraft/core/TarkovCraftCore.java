package tnt.tarkovcraft.core;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tnt.tarkovcraft.core.common.TarkovCraftCoreConfig;
import tnt.tarkovcraft.core.common.init.BaseItemStackFilters;
import tnt.tarkovcraft.core.common.init.BaseMailMessageAttachments;
import tnt.tarkovcraft.core.common.init.BaseTradeResources;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

@Mod(TarkovCraftCore.MOD_ID)
public class TarkovCraftCore {

    public static final String MOD_ID = "tarkovcraft_core";
    public static final Logger LOGGER = LogManager.getLogger("TarkovCraftCore");

    private static TarkovCraftCoreConfig config;

    public TarkovCraftCore(IEventBus modEventBus, ModContainer container) {
        // Configuration init
        config = Configuration.registerConfig(TarkovCraftCoreConfig.class, ConfigFormats.YAML).getConfigInstance();

        // Mod event listeners
        modEventBus.addListener(this::registerCustomRegistries);

        // Deferred registries
        BaseItemStackFilters.REGISTRY.register(modEventBus);
        BaseMailMessageAttachments.REGISTRY.register(modEventBus);
        BaseTradeResources.REGISTRY.register(modEventBus);
    }

    public static TarkovCraftCoreConfig getConfig() {
        return config;
    }

    public static ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void registerCustomRegistries(NewRegistryEvent event) {
        // Utils
        event.register(TarkovCraftRegistries.ITEMSTACK_FILTER);

        // Mail system
        event.register(TarkovCraftRegistries.MAIL_MESSAGE_ATTACHMENT);

        // Trading
        event.register(TarkovCraftRegistries.TRADE_RESOURCE);
        event.register(TarkovCraftRegistries.TRADE_CONDITION);
    }
}
