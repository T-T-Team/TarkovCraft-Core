package tnt.tarkovcraft.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.EntityInteraction;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;
import tnt.tarkovcraft.core.api.event.client.RegisterPostShaderProgramsEvent;
import tnt.tarkovcraft.core.api.event.client.ScreenSynchronizeEvent;
import tnt.tarkovcraft.core.client.config.TarkovCraftCoreClientConfig;
import tnt.tarkovcraft.core.client.notification.NotificationChannel;
import tnt.tarkovcraft.core.client.notification.NotificationLayer;
import tnt.tarkovcraft.core.client.overlay.DebugLayer;
import tnt.tarkovcraft.core.client.overlay.OnScreenHintLayer;
import tnt.tarkovcraft.core.client.overlay.StaminaLayer;
import tnt.tarkovcraft.core.client.screen.EntityInteractScreen;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.shader.BlindnessPostShaderProgram;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.init.CoreAttributes;

import java.util.List;

import static tnt.tarkovcraft.core.util.helper.TextHelper.createKeybindName;

@Mod(value = TarkovCraftCore.MOD_ID, dist = Dist.CLIENT)
public final class TarkovCraftCoreClient {

    public static final String SHARED_CATEGORY = "key.category.tarkovcraft.keymap";
    public static final KeyMapping KEY_CHARACTER = new KeyMapping(
            createKeybindName(TarkovCraftCore.MOD_ID, "character"),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            SHARED_CATEGORY
    );

    private static TarkovCraftCoreClientConfig config;

    private OnScreenHintLayer hintUiLayer;

    public TarkovCraftCoreClient(IEventBus modEventBus, ModContainer container) {
        config = Configuration.registerConfig(TarkovCraftCoreClientConfig.class, ConfigFormats.YAML).getConfigInstance();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerKeyBindings);
        modEventBus.addListener(this::registerCustomGuiLayers);
        modEventBus.addListener(this::registerShaderPrograms);

        NeoForge.EVENT_BUS.addListener(this::onKeyboardInput);
        NeoForge.EVENT_BUS.addListener(this::clientPostTick);
        NeoForge.EVENT_BUS.addListener(this::clientLoggedOut);
        NeoForge.EVENT_BUS.addListener(this::onPlaySound);
    }

    public static TarkovCraftCoreClientConfig getConfig() {
        return config;
    }

    public static void synchronizeCurrentScreen(SynchronizableScreen.DataSource dataSource) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = minecraft.screen;
        if (screen instanceof SynchronizableScreen synchronizableScreen) {
            synchronizableScreen.sync(dataSource);
            NeoForge.EVENT_BUS.post(new ScreenSynchronizeEvent(screen, dataSource));
        }
    }

    public static void openEntityInteractionScreen(LivingEntity entity, List<EntityInteraction.Type<?>> interactions) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new EntityInteractScreen(entity, interactions));
    }

    private void setup(FMLClientSetupEvent event) {
        PostEffectShaderProgramProcessor.INSTANCE.init(config.enableCustomShaders);
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
        // on-screen guide tick
        if (minecraft.level != null) {
            this.hintUiLayer.tick();
            // shader program tick
            PostEffectShaderProgramProcessor.INSTANCE.tick();
        }
    }

    private void clientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        NotificationChannel.MAIN.clearAllNotifications();
    }

    private void registerShaderPrograms(RegisterPostShaderProgramsEvent event) {
        event.register(BlindnessPostShaderProgram.INSTANCE);
    }

    private void onPlaySound(PlaySoundEvent event) {
        Minecraft client = Minecraft.getInstance();
        Entity cameraEntity = client.getCameraEntity();
        if (cameraEntity == null)
            return;
        float hearing = AttributeSystem.getFloatValue(cameraEntity, CoreAttributes.HEARING, 1.0F);
        if (hearing <= 0.0F) {
            event.setSound(null);
            return;
        }
        float distortion = AttributeSystem.getFloatValue(cameraEntity, CoreAttributes.HEARING_DISTORTION, 1.0F);
        if (hearing == 1.0F && distortion == 1.0F)
            return;
        SoundInstance instance = event.getSound();
        if (instance instanceof AbstractSoundInstance abstractSoundInstance) {
            abstractSoundInstance.volume = abstractSoundInstance.volume * hearing;
            abstractSoundInstance.pitch = abstractSoundInstance.pitch * distortion;
        }
    }
}
