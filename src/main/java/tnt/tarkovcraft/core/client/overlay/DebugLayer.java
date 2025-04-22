package tnt.tarkovcraft.core.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.energy.AbstractStamina;
import tnt.tarkovcraft.core.common.energy.ArmStamina;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.common.energy.MovementStamina;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

import java.util.Locale;

public class DebugLayer implements LayeredDraw.Layer {

    public static final ResourceLocation LAYER_ID = TarkovCraftCore.createResourceLocation("layer/debug");
    private int line;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!TarkovCraftCoreClient.getConfig().renderDebugOverlay)
            return;
        line = 0;
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        Player player = client.player;

        EntityAttributeData attributeData = player.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        guiGraphics.drawString(font, "Move Stamina Component: " + EnergySystem.MOVEMENT_STAMINA, 5, y(), 0xFFFFFF);
        guiGraphics.drawString(font, "Arm Stamina Component: " + EnergySystem.ARM_STAMINA, 5, y(), 0xFFFFFF);
        renderEnergyStats(font, guiGraphics, player);

        renderCoreAttributeData(font, guiGraphics, attributeData);
    }

    private void renderEnergyStats(Font font, GuiGraphics graphics, Player player) {
        if (EnergySystem.MOVEMENT_STAMINA.isVanilla()) {
            MovementStamina moveStamina = player.getData(CoreDataAttachments.MOVEMENT_STAMINA);
            renderStaminaInfo(font, graphics, player, moveStamina);
        }
        if (EnergySystem.ARM_STAMINA.isVanilla()) {
            ArmStamina armStamina = player.getData(CoreDataAttachments.ARM_STAMINA);
            renderStaminaInfo(font, graphics, player, armStamina);
        }
        ++line;
    }

    private void renderCoreAttributeData(Font font, GuiGraphics graphics, EntityAttributeData attributeData) {
        graphics.drawString(font, "Core Attributes", 5, y(), 0xFFFFFF);
        float forgetRate = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_TIME_MULTIPLIER).floatValue();
        graphics.drawString(font, String.format(Locale.ROOT, "Memory forget rate: %.2f", forgetRate), 5, y(), 0xFFFFFF);
        float forgetAmount = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_AMOUNT_MULTIPLIER).floatValue();
        graphics.drawString(font, String.format(Locale.ROOT, "Memory forget amount: %.2f", forgetAmount), 5, y(), 0xFFFFFF);
        ++line;
    }

    private void renderStaminaInfo(Font font, GuiGraphics graphics, Player player, AbstractStamina energy) {
        float current = energy.getStamina();
        float max = energy.getMaxStamina(player);
        float consumption = energy.getConsumptionMultiplier(player);
        String value = String.format(Locale.ROOT, "%s: %.2f/%.2f | C %.3f", energy.getStaminaType(), current, max, consumption);
        graphics.drawString(font, value, 5, y(), 0xFFFFFF);
    }

    private int y() {
        return 5 + 10 * line++;
    }
}
