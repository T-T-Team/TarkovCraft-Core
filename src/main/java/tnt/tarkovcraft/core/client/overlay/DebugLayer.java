package tnt.tarkovcraft.core.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.energy.MovementStamina;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

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
        renderEnergyStats(font, guiGraphics, player, attributeData);

        renderCoreAttributeData(font, guiGraphics, attributeData);

        SkillData skillData = player.getData(CoreDataAttachments.SKILL);
        renderSkillData(font, client.getConnection().registryAccess(), guiGraphics, skillData);
    }

    private void renderEnergyStats(Font font, GuiGraphics graphics, Player player, EntityAttributeData attributeData) {
        MovementStamina stamina = player.getData(CoreDataAttachments.STAMINA);
        graphics.drawString(font, "Energy", 5, y(), 0xFFFFFF);
        renderStaminaInfo(font, graphics, attributeData, stamina);
        ++line;
    }

    private void renderCoreAttributeData(Font font, GuiGraphics graphics, EntityAttributeData attributeData) {
        graphics.drawString(font, "Core Attributes", 5, y(), 0xFFFFFF);
        AttributeInstance sprintEnabled = attributeData.getAttribute(CoreAttributes.SPRINT);
        boolean enabled = sprintEnabled.booleanValue();
        graphics.drawString(font, "Sprint: " + enabled, 5, y(), 0xFFFFFF);
        ++line;
    }

    private void renderStaminaInfo(Font font, GuiGraphics graphics, EntityAttributeData attributeData, MovementStamina energy) {
        float current = energy.getStamina();
        float max = energy.getMaxStamina(attributeData);
        float recovery = energy.getRecoveryAmount(attributeData);
        float consumption = energy.getConsumptionMultiplier(attributeData);
        String value = String.format(Locale.ROOT, "%s: %.2f/%.2f | R %.3f | C %.3f", "Move Stamina", current, max, recovery, consumption);
        graphics.drawString(font, value, 5, y(), 0xFFFFFF);
    }

    private void renderSkillData(Font font, RegistryAccess access, GuiGraphics graphics, SkillData data) {
        ResourceKey<SkillDefinition> rk = ResourceKey.create(CoreRegistries.DatapackKeys.SKILL_DEFINITION, TarkovCraftCore.createResourceLocation("sprinting"));
        Holder<SkillDefinition> holder = access.holderOrThrow(rk);
        SkillDefinition definition = holder.value();

        Skill skill = data.getSkill(definition);
        String info = String.format(Locale.ROOT, "%s: L%d | %.3f/%.1f", definition.getName().getString(), skill.getLevel(), skill.getExperience(), skill.getRequiredExperience());
        graphics.drawString(font, info, 5, y(), 0xFFFFFF);

        ++line;
    }

    private int y() {
        return 5 + 10 * line++;
    }
}
