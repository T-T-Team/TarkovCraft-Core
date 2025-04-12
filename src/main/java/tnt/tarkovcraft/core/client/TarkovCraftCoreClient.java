package tnt.tarkovcraft.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;
import tnt.tarkovcraft.core.client.notification.NotificationLayer;
import tnt.tarkovcraft.core.client.overlay.DebugLayer;
import tnt.tarkovcraft.core.client.screen.CharacterScreen;
import tnt.tarkovcraft.core.client.screen.DataScreen;
import tnt.tarkovcraft.core.network.Synchronizable;

import static tnt.tarkovcraft.core.util.helper.LocalizationHelper.createKeybindName;

@Mod(value = TarkovCraftCore.MOD_ID, dist = Dist.CLIENT)
public final class TarkovCraftCoreClient {

    public static final KeyMapping KEY_CHARACTER = new KeyMapping(createKeybindName(TarkovCraftCore.MOD_ID, "character"), KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, TarkovCraftCore.GLOBAL_CATEGORY_KEY);

    private static TarkovCraftCoreClientConfig config;

    public TarkovCraftCoreClient(IEventBus modEventBus, ModContainer container) {
        config = Configuration.registerConfig(TarkovCraftCoreClientConfig.class, ConfigFormats.YAML).getConfigInstance();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerKeyBindings);
        modEventBus.addListener(this::registerCustomGuiLayers);

        NeoForge.EVENT_BUS.addListener(this::onKeyboardInput);
        NeoForge.EVENT_BUS.register(new TarkovCraftCoreClientEventHandler());
    }

    public static TarkovCraftCoreClientConfig getConfig() {
        return config;
    }

    private void dispatchParallelRegistryEvents() {
    }

    private void setup(FMLClientSetupEvent event) {
        this.dispatchParallelRegistryEvents();
    }

    private void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(KEY_CHARACTER);
    }

    private void onKeyboardInput(InputEvent.Key event) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        // Game keybinds
        if (player != null) {
            if (KEY_CHARACTER.consumeClick()) {
                client.setScreen(new CharacterScreen());
            }
        }
    }

    private void registerCustomGuiLayers(RegisterGuiLayersEvent event) {
        if (!FMLEnvironment.production)
            event.registerAboveAll(DebugLayer.LAYER_ID, new DebugLayer());
        event.registerAboveAll(NotificationLayer.LAYER_ID, new NotificationLayer(NotificationChannel.MAIN));
    }

    public static void sendDataSyncEvent(Entity entity, AttachmentType<?> type, Synchronizable<?> data) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen instanceof DataScreen dataScreen) {
            dataScreen.onAttachmentDataReceived(entity, type, data);
        }
    }
}
