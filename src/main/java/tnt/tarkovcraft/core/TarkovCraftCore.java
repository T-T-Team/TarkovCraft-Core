package tnt.tarkovcraft.core;

import com.mojang.brigadier.CommandDispatcher;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tnt.tarkovcraft.core.common.TarkovCraftCommand;
import tnt.tarkovcraft.core.common.config.TarkovCraftCoreConfig;
import tnt.tarkovcraft.core.common.init.*;
import tnt.tarkovcraft.core.network.TarkovCraftCoreNetwork;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.Arrays;

@Mod(TarkovCraftCore.MOD_ID)
public class TarkovCraftCore {

    public static final String MOD_ID = "tarkovcraft_core";
    public static final String GLOBAL_CATEGORY_KEY = "category.tarkovcraft";
    public static final Logger LOGGER = LogManager.getLogger("TarkovCraftCore");

    private static TarkovCraftCoreConfig config;

    public TarkovCraftCore(IEventBus modEventBus, ModContainer container) {
        // Configuration init
        config = Configuration.registerConfig(TarkovCraftCoreConfig.class, ConfigFormats.YAML).getConfigInstance();

        // Mod event listeners
        modEventBus.addListener(this::registerCustomRegistries);
        modEventBus.addListener(TarkovCraftCoreNetwork::onRegistration);

        // Neoforge event listeners
        NeoForge.EVENT_BUS.register(this);

        // Deferred registries
        BaseAttributeModifiers.REGISTRY.register(modEventBus);
        BaseItemStackFilters.REGISTRY.register(modEventBus);
        BaseMailMessageAttachments.REGISTRY.register(modEventBus);
        BaseTradeResources.REGISTRY.register(modEventBus);
        BaseDataAttachments.REGISTRY.register(modEventBus);
    }

    public static TarkovCraftCoreConfig getConfig() {
        return config;
    }

    public static ResourceLocation createResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void registerCustomRegistries(NewRegistryEvent event) {
        // Utils
        event.register(TarkovCraftRegistries.ATTRIBUTE);
        event.register(TarkovCraftRegistries.ATTRIBUTE_MODIFIER);
        event.register(TarkovCraftRegistries.ITEMSTACK_FILTER);

        // Mail system
        event.register(TarkovCraftRegistries.MAIL_MESSAGE_ATTACHMENT);

        // Trading
        event.register(TarkovCraftRegistries.TRADE_RESOURCE);
        event.register(TarkovCraftRegistries.TRADE_CONDITION);
    }

    @SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        TarkovCraftCommand.create(dispatcher);
    }

    @SubscribeEvent
    private void onPlayerLoggingIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide())
            return;
        S2C_SendDataAttachments payload = new S2C_SendDataAttachments(player, Arrays.asList(
                BaseDataAttachments.MAIL_MANAGER.get()
        ));
        PacketDistributor.sendToPlayer((ServerPlayer) player, payload);
    }
}
