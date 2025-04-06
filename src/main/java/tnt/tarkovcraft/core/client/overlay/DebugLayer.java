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
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.energy.Energy;
import tnt.tarkovcraft.core.common.energy.EnergyData;
import tnt.tarkovcraft.core.common.energy.EnergyType;
import tnt.tarkovcraft.core.common.init.BaseAttributes;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;

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

        EntityAttributeData attributeData = player.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        EnergyData energyData = player.getData(BaseDataAttachments.ENERGY);
        renderEnergyStats(font, guiGraphics, attributeData, energyData);

        renderCoreAttributeData(font, guiGraphics, attributeData);
    }

    private void renderEnergyStats(Font font, GuiGraphics graphics, EntityAttributeData attributeData, EnergyData energyData) {
        graphics.drawString(font, "Energy", 5, y(), 0xFFFFFF);
        renderEnergyStat(font, graphics, attributeData, energyData.getEnergyValue(EnergyType.LEG_STAMINA), "Leg");
        renderEnergyStat(font, graphics, attributeData, energyData.getEnergyValue(EnergyType.ARM_STAMINA), "Arm");
        ++line;
    }

    private void renderCoreAttributeData(Font font, GuiGraphics graphics, EntityAttributeData attributeData) {
        graphics.drawString(font, "Core Attributes", 5, y(), 0xFFFFFF);
        AttributeInstance sprintEnabled = attributeData.getAttribute(BaseAttributes.SPRINT);
        boolean enabled = sprintEnabled.booleanValue();
        graphics.drawString(font, "Sprint: " + enabled, 5, y(), 0xFFFFFF);
        ++line;
    }

    private void renderEnergyStat(Font font, GuiGraphics graphics, EntityAttributeData attributeData, Energy energy, String name) {
        float current = energy.getEnergy();
        float max = energy.getMaxEnergy(attributeData);
        float recovery = energy.getRecoveryAmount(attributeData);
        float consumption = energy.getConsumptionMultiplier(attributeData);
        String value = String.format(Locale.ROOT, "%s: %.2f/%.2f | R %.3f | C %.3f", name, current, max, recovery, consumption);
        graphics.drawString(font, value, 5, y(), 0xFFFFFF);
    }

    private int y() {
        return 5 + 10 * line++;
    }
}
