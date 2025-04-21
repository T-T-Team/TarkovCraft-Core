package tnt.tarkovcraft.core;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.common.TarkovCraftCoreEventHandler;
import tnt.tarkovcraft.core.common.config.TarkovCraftCoreConfig;
import tnt.tarkovcraft.core.common.init.*;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.statistic.DisplayStatistic;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;

@Mod(TarkovCraftCore.MOD_ID)
public class TarkovCraftCore {

    public static final String MOD_ID = "tarkovcraft_core";
    public static final String GLOBAL_CATEGORY_KEY = "category.tarkovcraft";
    public static final Logger LOGGER = LogManager.getLogger("TarkovCraftCore");
    public static final Marker MARKER = MarkerManager.getMarker("Core");

    private static TarkovCraftCoreConfig config;

    public TarkovCraftCore(IEventBus modEventBus, ModContainer container) {
        // Configuration init
        config = Configuration.registerConfig(TarkovCraftCoreConfig.class, ConfigFormats.YAML).getConfigInstance();

        // Mod event listeners
        modEventBus.addListener(this::registerCustomRegistries);
        modEventBus.addListener(this::registerCustomDatapackRegistries);
        modEventBus.addListener(TarkovCraftCoreNetwork::onRegistration);

        // Neoforge event listeners
        NeoForge.EVENT_BUS.register(new TarkovCraftCoreEventHandler());

        // Deferred registries
        CoreAttributes.REGISTRY.register(modEventBus);
        CoreAttributeModifiers.REGISTRY.register(modEventBus);
        CoreItemStackFilters.REGISTRY.register(modEventBus);
        CoreNumberProviders.REGISTRY.register(modEventBus);
        CoreMailMessageAttachments.REGISTRY.register(modEventBus);
        CoreDataAttachments.REGISTRY.register(modEventBus);
        CoreSkillTriggerEvents.REGISTRY.register(modEventBus);
        CoreSkillTrackers.REGISTRY.register(modEventBus);
        CoreSkillTriggerConditions.REGISTRY.register(modEventBus);
        CoreSkillStatConditions.REGISTRY.register(modEventBus);
        CoreSkillStats.REGISTRY.register(modEventBus);
        CoreStatistics.REGISTRY.register(modEventBus);
    }

    public static TarkovCraftCoreConfig getConfig() {
        return config;
    }

    public static ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void registerCustomRegistries(NewRegistryEvent event) {
        // Utils
        event.register(CoreRegistries.ATTRIBUTE);
        event.register(CoreRegistries.ATTRIBUTE_MODIFIER);
        event.register(CoreRegistries.ITEMSTACK_FILTER);
        event.register(CoreRegistries.NUMBER_PROVIDER);
        event.register(CoreRegistries.STATISTICS);

        // Mail system
        event.register(CoreRegistries.MAIL_MESSAGE_ATTACHMENT);

        // Skill system
        event.register(CoreRegistries.SKILL_TRIGGER_EVENT);
        event.register(CoreRegistries.SKILL_TRIGGER_TYPE);
        event.register(CoreRegistries.SKILL_TRIGGER_CONDITION_TYPE);
        event.register(CoreRegistries.SKILL_STAT_CONDITION_TYPE);
        event.register(CoreRegistries.SKILL_STAT);
    }

    private void registerCustomDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(CoreRegistries.DatapackKeys.SKILL_DEFINITION, SkillDefinition.DIRECT_CODEC, SkillDefinition.DIRECT_CODEC);
        event.dataPackRegistry(CoreRegistries.DatapackKeys.DISPLAY_STATISTIC, DisplayStatistic.CODEC, DisplayStatistic.CODEC);
    }
}
