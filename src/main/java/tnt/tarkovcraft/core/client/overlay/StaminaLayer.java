package tnt.tarkovcraft.core.client.overlay;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector2f;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.StaminaComponent;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.client.config.StaminaConfigurableOverlay;
import tnt.tarkovcraft.core.client.config.TarkovCraftCoreClientConfig;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.compatibility.CompatibilityComponent;

public class StaminaLayer implements LayeredDraw.Layer {

    public static final ResourceLocation LAYER_ID = TarkovCraftCore.createResourceLocation("layer/stamina");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!EnergySystem.isEnabled())
            return;

        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();
        Entity entity = client.cameraEntity;
        if (client.player.isSpectator() && client.player == entity) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            TarkovCraftCoreClientConfig clientConfig = TarkovCraftCoreClient.getConfig();
            renderStaminaOverlay(guiGraphics, window, clientConfig.moveStaminaOverlay, livingEntity, EnergySystem.MOVEMENT_STAMINA);
            renderStaminaOverlay(guiGraphics, window, clientConfig.armStaminaOverlay, livingEntity, EnergySystem.ARM_STAMINA);
        }
    }

    private void renderStaminaOverlay(GuiGraphics graphics, Window window, StaminaConfigurableOverlay overlay, LivingEntity entity, CompatibilityComponent<? extends StaminaComponent> intgComponent) {
        if (!overlay.enabled)
            return;
        StaminaComponent component = intgComponent.getComponent();
        if (!component.isAttached(entity))
            return;
        float stamina = component.getStamina(entity);
        float maxStamina = component.getMaxStamina(entity);
        boolean critical = component.isCriticalValue(entity);

        // background
        long background = Long.decode(overlay.backgroundColor);
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();
        int overlayWidth = 80;
        int overlayHeight = 4;
        Vector2f bgPos = overlay.getPosition(0, 0, width, height, overlayWidth, overlayHeight);
        graphics.fill((int) bgPos.x, (int) bgPos.y, (int) (bgPos.x + overlayWidth), (int) (bgPos.y + overlayHeight), (int) background);

        // status
        long gradientMin = Long.decode(critical ? overlay.barGradientStartCriticalColor : overlay.barGradientStartColor);
        long gradientMax = Long.decode(critical ? overlay.barGradientEndCriticalColor : overlay.barGradientEndColor);
        int gradientWidth = (int) ((overlayWidth - 2) * (stamina / maxStamina));
        graphics.fillGradient((int) bgPos.x + 1, (int) bgPos.y + 1, (int) bgPos.x + 1 + gradientWidth, (int) (bgPos.y + overlayHeight - 1), (int) gradientMin, (int) gradientMax);
    }
}
