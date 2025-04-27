package tnt.tarkovcraft.core.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.client.screen.widget.ScrollbarWidget;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillContextKeys;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatDefinition;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatDisplayInformation;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;
import tnt.tarkovcraft.core.util.context.ContextKeys;
import tnt.tarkovcraft.core.util.helper.MathHelper;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SkillScreen extends CharacterSubScreen {

    private SkillData skillData;

    private double scroll;

    public SkillScreen(Context context) {
        super(context.getOrThrow(ContextKeys.UUID), CoreNavigators.SKILL_ENTRY);
    }

    @Override
    protected void init() {
        super.init();

        Player player = this.minecraft.level.getPlayerByUUID(this.characterProfileId);
        if (player == null)
            return;
        this.skillData = player.getData(CoreDataAttachments.SKILL);
        Registry<SkillDefinition> registry = this.minecraft.getConnection().registryAccess().lookupOrThrow(CoreRegistries.DatapackKeys.SKILL_DEFINITION);
        List<Skill> skills = registry.listElements().map(reference -> this.skillData.getSkill(reference.value())).toList();

        ListWidget<SkillWidget> skillView = this.addRenderableWidget(new ListWidget<>(0, 25, this.width - 4, this.height - 25, skills, (skill, i) -> this.buildSkillWidget(player, skill, i)));
        skillView.setBackgroundColor(ColorPalette.BG_TRANSPARENT_WEAK);
        skillView.setAdditionalItemSpacing(5);
        skillView.setScrollListener((x, y) -> this.scroll = y);
        skillView.setScroll(this.scroll);

        ScrollbarWidget scrollbar = this.addRenderableWidget(new ScrollbarWidget(this.width - 4, 25, 4, this.height - 25, skillView));
        scrollbar.setBackground(ColorPalette.BG_TRANSPARENT_WEAK);
    }

    private SkillWidget buildSkillWidget(Player player, Skill skill, int index) {
        Context context = ContextImpl.of(
                ContextKeys.LEVEL, player.level(),
                ContextKeys.ENTITY, player,
                SkillContextKeys.DEFINITION, skill.getDefinition().value(),
                SkillContextKeys.SKILL, skill
        );
        return new SkillWidget(5, 5 + index * 40, this.width - 15, 35, this.font, skill, this, context);
    }

    public static final class SkillWidget extends AbstractWidget {

        private final Context context;
        private final Screen screen;
        private final Font font;
        private final Skill skill;
        private final ResourceLocation skillIcon;

        public SkillWidget(int x, int y, int width, int height, Font font, Skill skill, Screen screen, Context context) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.font = font;
            this.skill = skill;
            this.screen = screen;
            this.context = context;
            MutableComponent title = skill.getDefinition().value().getName().copy();
            this.setMessage(title.withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE));
            this.skillIcon = SkillDefinition.getIcon(skill.getDefinition());
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            boolean isMaxLevel = this.skill.isMaxLevel();
            int frameColor = isMaxLevel ? 0xFFFFFF00 : 0xFF888888;
            // Skill name
            guiGraphics.drawString(this.font, this.getMessage(), this.getX() + this.height + 3, this.getY() + 1, ColorPalette.WHITE, true);
            // Skill frame + icon
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.height, this.getY() + this.height, frameColor);
            RenderUtils.blitFull(guiGraphics, this.skillIcon, this.getX() + 1, this.getY() + 1, this.getX() + this.height - 1, this.getY() + this.height - 1, -1);
            // Experience bar
            guiGraphics.fillGradient(this.getX() + this.height + 2, this.getY() + 13, this.getRight(), this.getBottom() - 11, ARGB.opaque(ColorPalette.TEXT_COLOR_DISABLED), ARGB.scaleRGB(ARGB.opaque(ColorPalette.TEXT_COLOR_DISABLED), 0.6F));
            float experienceProgress = isMaxLevel ? 1.0F : this.skill.getExperience() / this.skill.getRequiredExperience();
            int width = (this.getRight() - 1 - (this.getX() + this.height + 3));
            int expColor = ARGB.opaque(0xE8CE31);
            guiGraphics.fillGradient(this.getX() + this.height + 3, this.getY() + 14, this.getX() + this.height + 3 + Mth.ceil(experienceProgress * width), this.getBottom() - 12, expColor, ARGB.scaleRGB(expColor, 0.8F));
            // Experience text
            if (!isMaxLevel) {
                String expLabel = String.format(Locale.ROOT, "%.1f / %.1f", this.skill.getExperience(), this.skill.getRequiredExperience());
                guiGraphics.drawString(this.font, expLabel, this.getRight() - this.font.width(expLabel), this.getBottom() - 9, ARGB.scaleRGB(ColorPalette.TEXT_COLOR, 0.7F), false);
            }
            // Level text
            Component levelMessage = isMaxLevel ? Skill.MAX_LEVEL : Component.translatable("label.tarkovcraft_core.skill.level", this.skill.getLevel(), this.skill.getMaxLevel()).withColor(ColorPalette.TEXT_COLOR);
            guiGraphics.drawString(this.font, levelMessage, this.getX() + this.height + 3, this.getBottom() - 9, ColorPalette.WHITE, false);
            // Badges + badge hover
            int index = 0;
            SkillDefinition definition = this.skill.getDefinition().value();
            for (SkillStatDefinition statDefinition : definition.getStats()) {
                if (!statDefinition.isAvailable(this.context))
                    continue;
                SkillStatDisplayInformation displayInfo = statDefinition.display();
                int left = this.getRight() - 10 - index * 12;
                int right = left + 10;
                int top = this.getY();
                int bottom = top + 10;
                guiGraphics.fillGradient(left, top, right, bottom, ARGB.opaque(displayInfo.color()), ARGB.scaleRGB(ARGB.opaque(displayInfo.color()), 0.75F));
                RenderUtils.blitFull(guiGraphics, displayInfo.icon(), left + 1, top + 1, right - 1, bottom - 1, -1);
                if (MathHelper.isWithinBounds(mouseX, mouseY, left, top, right - left, bottom - top)) {
                    Component name = displayInfo.name().copy().withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.YELLOW);
                    Component statDescription = displayInfo.getDescription(this.context, statDefinition.stat());
                    List<FormattedCharSequence> tooltip = new ArrayList<>();
                    tooltip.addAll(Tooltip.splitTooltip(this.screen.getMinecraft(), name));
                    tooltip.addAll(Tooltip.splitTooltip(this.screen.getMinecraft(), statDescription));
                    this.screen.setTooltipForNextRenderPass(tooltip);
                }
                ++index;
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
