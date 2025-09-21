package tnt.tarkovcraft.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.toma.configuration.Configuration;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.config.TarkovCraftCoreClientConfig;
import tnt.tarkovcraft.core.client.decoration.CurrencyItemDecorator;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;
import tnt.tarkovcraft.core.client.notification.NotificationLayer;
import tnt.tarkovcraft.core.client.overlay.DebugLayer;
import tnt.tarkovcraft.core.client.overlay.OnScreenHintLayer;
import tnt.tarkovcraft.core.client.overlay.StaminaLayer;
import tnt.tarkovcraft.core.client.screen.DataScreen;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.common.item.CurrencyItem;
import tnt.tarkovcraft.core.network.Synchronizable;

import static tnt.tarkovcraft.core.util.helper.TextHelper.createKeybindName;

@Mod(value = TarkovCraftCore.MOD_ID, dist = Dist.CLIENT)
public final class TarkovCraftCoreClient {

    public static final KeyMapping KEY_CHARACTER = new KeyMapping(createKeybindName(TarkovCraftCore.MOD_ID, "character"), KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, TarkovCraftCore.GLOBAL_CATEGORY_KEY);

    private static TarkovCraftCoreClientConfig config;

    private OnScreenHintLayer hintUiLayer;

    public TarkovCraftCoreClient(IEventBus modEventBus, ModContainer container) {
        config = Configuration.registerSimpleYmlConfig(TarkovCraftCoreClientConfig.class);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerKeyBindings);
        modEventBus.addListener(this::registerCustomGuiLayers);
        modEventBus.addListener(this::registerItemDecorators);

        NeoForge.EVENT_BUS.addListener(this::onKeyboardInput);
        NeoForge.EVENT_BUS.addListener(this::clientPostTick);
        NeoForge.EVENT_BUS.addListener(this::clientLoggedOut);
    }

    public static TarkovCraftCoreClientConfig getConfig() {
        return config;
    }

    public static void sendDataSyncEvent(Entity entity, AttachmentType<?> type, Synchronizable<?> data) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen instanceof DataScreen dataScreen) {
            dataScreen.onAttachmentDataReceived(entity, type, data);
        }
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
                client.setScreen(CoreNavigators.CHARACTER_NAVIGATION_PROVIDER.buildInitial(null, player.getUUID()));
            }
        }
    }

    private void registerCustomGuiLayers(RegisterGuiLayersEvent event) {
        if (!FMLEnvironment.production)
            event.registerAboveAll(DebugLayer.LAYER_ID, new DebugLayer());
        event.registerAboveAll(NotificationLayer.LAYER_ID, new NotificationLayer(NotificationChannel.MAIN));
        event.registerAboveAll(StaminaLayer.LAYER_ID, new StaminaLayer());
        this.hintUiLayer = new OnScreenHintLayer();
        event.registerAboveAll(OnScreenHintLayer.LAYER_ID, this.hintUiLayer);
    }

    private void clientPostTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;

        // notification tick
        if (screen == null) {
            NotificationChannel.MAIN.update();
        }
        if (minecraft.level != null) {
            this.hintUiLayer.tick();
        }
    }

    private void clientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        NotificationChannel.MAIN.clearAllNotifications();
    }

    private void registerItemDecorators(RegisterItemDecorationsEvent event) {
        CurrencyItemDecorator decorator = new CurrencyItemDecorator();
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof CurrencyItem)
                .forEach(item -> event.register(item, decorator));
    }
}
