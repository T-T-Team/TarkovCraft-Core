package tnt.tarkovcraft.core.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.GuiLayer;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.StaminaComponent;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

import java.util.Locale;

public class DebugLayer implements GuiLayer {

    public static final Identifier LAYER_ID = TarkovCraftCore.createIdentifier("layer/debug");
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
        guiGraphics.drawString(font, "Move Stamina Component: " + EnergySystem.MOVEMENT_STAMINA, 5, y(), 0xFFFFFFFF);
        guiGraphics.drawString(font, "Arm Stamina Component: " + EnergySystem.ARM_STAMINA, 5, y(), 0xFFFFFFFF);
        renderEnergyStats(font, guiGraphics, player);

        renderCoreAttributeData(font, guiGraphics, attributeData);
    }

    private void renderEnergyStats(Font font, GuiGraphics graphics, Player player) {
        this.renderStaminaInfo(font, graphics, player, "Sprint stamina", EnergySystem.MOVEMENT_STAMINA.getComponent());
        this.renderStaminaInfo(font, graphics, player, "Arm stamina", EnergySystem.ARM_STAMINA.getComponent());
        ++line;
    }

    private void renderCoreAttributeData(Font font, GuiGraphics graphics, EntityAttributeData attributeData) {
        graphics.drawString(font, "Core Attributes", 5, y(), 0xFFFFFFFF);
        float forgetRate = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_TIME_MULTIPLIER).floatValue();
        graphics.drawString(font, String.format(Locale.ROOT, "Memory forget rate: %.2f", forgetRate), 5, y(), 0xFFFFFFFF);
        float forgetAmount = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_AMOUNT_MULTIPLIER).floatValue();
        graphics.drawString(font, String.format(Locale.ROOT, "Memory forget amount: %.2f", forgetAmount), 5, y(), 0xFFFFFFFF);
        ++line;
    }

    private void renderStaminaInfo(Font font, GuiGraphics graphics, Player player, String name, StaminaComponent component) {
        if (component.shouldRenderOverlay(player)) {
            float current = component.getStamina(player);
            float max = component.getMaxStamina(player);
            String value = String.format(Locale.ROOT, "%s: %.2f/%.2f", name, current, max);
            graphics.drawString(font, value, 5, y(), 0xFFFFFFFF);
        }
    }

    private int y() {
        return 5 + 10 * line++;
    }
}
