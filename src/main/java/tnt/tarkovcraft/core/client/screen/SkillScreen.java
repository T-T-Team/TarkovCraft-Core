package tnt.tarkovcraft.core.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.client.screen.widget.ScrollbarWidget;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillData;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;
import tnt.tarkovcraft.core.common.skill.bonus.SkillBonusDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerDefinition;
import tnt.tarkovcraft.core.util.helper.Helper;
import tnt.tarkovcraft.core.util.helper.MathHelper;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

import java.util.*;

public class SkillScreen extends CharacterSubScreen {

    private SkillData skillData;

    private double scroll;

    public SkillScreen(UUID userId) {
        super(userId, CoreNavigators.SKILL_ENTRY);
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
        skillView.setScrollListener((_, y) -> this.scroll = y);
        skillView.setScroll(this.scroll);

        ScrollbarWidget scrollbar = this.addRenderableWidget(new ScrollbarWidget(this.width - 4, 25, 4, this.height - 25, skillView));
        scrollbar.setBackground(ColorPalette.BG_TRANSPARENT_WEAK);

        this.initNotificationLayer();
    }

    @Override
    public void sync(DataSource source) {
        if (!source.equals(SKILLS))
            return;
        this.init(this.width, this.height);
    }

    private SkillWidget buildSkillWidget(Player player, Skill skill, int index) {
        SkillWidget widget = new SkillWidget(5, 5 + index * 40, this.width - 15, 35, this.font, skill, player);
        SkillDefinition definition = skill.getDefinition().value();
        Collection<SkillTriggerDefinition> triggers = definition.triggers();
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(definition.getFormattedName(style -> style.applyFormats(ChatFormatting.BOLD, ChatFormatting.YELLOW)));
        tooltip.add(Component.translatable("tooltip.tarkovcraft_core.skill.skill_info").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE));
        triggers.stream().flatMap(def -> def.getInfoComponents().stream()).forEach(tooltip::add);
        widget.setDescription(tooltip);
        return widget;
    }

    public static final class SkillWidget extends AbstractWidget {

        private final Font font;
        private final Skill skill;
        private final Identifier skillIcon;
        private List<Component> description;
        private final List<BonusBadgeInfo> badges;

        public SkillWidget(int x, int y, int width, int height, Font font, Skill skill, Player player) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.font = font;
            this.skill = skill;
            this.setMessage(skill.getDefinition().value().getFormattedName(style -> style.applyFormats(ChatFormatting.BOLD, ChatFormatting.UNDERLINE)));
            Holder<SkillDefinition> holder = skill.getDefinition();
            this.skillIcon = SkillDefinition.getIcon(holder);
            this.badges = this.getBadges(holder, skill, player);
        }

        public void setDescription(List<Component> description) {
            this.description = description;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
            boolean isMaxLevel = this.skill.isMaxLevel();
            // Skill name
            guiGraphics.text(this.font, this.getMessage(), this.getX() + this.height + 3, this.getY() + 1, ColorPalette.WHITE, true);

            // Skill icon
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
                guiGraphics.text(this.font, expLabel, this.getRight() - this.font.width(expLabel), this.getBottom() - 9, ARGB.scaleRGB(ColorPalette.TEXT_COLOR, 0.7F), false);
            }

            // Level text
            Component levelMessage = isMaxLevel ? Skill.MAX_LEVEL : Component.translatable("label.tarkovcraft_core.skill.level", this.skill.getLevel(), this.skill.getMaxLevel()).withColor(ColorPalette.TEXT_COLOR);
            guiGraphics.text(this.font, levelMessage, this.getX() + this.height + 3, this.getBottom() - 9, ColorPalette.WHITE, false);

            // Bonus badges + hover display
            for (int i = 0; i < this.badges.size(); i++) {
                BonusBadgeInfo badge = this.badges.get(i);
                int leftPos = this.getRight() - 10 - i * 12;
                badge.extractRenderState(guiGraphics, this.font, leftPos, this.getY(), mouseX, mouseY);
            }

            // Skill info
            int iconSize = this.height;
            if (MathHelper.isWithinBounds(mouseX, mouseY, this.getX(), this.getY(), iconSize, iconSize) && Helper.isNotEmpty(this.description)) {
                guiGraphics.setTooltipForNextFrame(this.font, this.description, Optional.empty(), mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return false;
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }

        private List<BonusBadgeInfo> getBadges(Holder<SkillDefinition> holder, Skill skill, LivingEntity entity) {
            return holder.value().bonuses().stream()
                    .filter(bonus -> bonus.isAvailable(holder.value(), skill, entity))
                    .map(bonus -> new BonusBadgeInfo(holder, bonus, skill, entity))
                    .toList();
        }

        private static final class BonusBadgeInfo {

            private final Identifier icon;
            private final List<Component> tooltip;

            BonusBadgeInfo(Holder<SkillDefinition> holder, SkillBonusDefinition definition, Skill skill, LivingEntity entity) {
                this.icon = definition.getIcon(holder);
                Component name = definition.getDisplayName(holder).withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE);
                Component description = definition.getContextualDescription(holder, skill, entity).withStyle(ChatFormatting.GRAY);
                this.tooltip = Arrays.asList(name, description);
            }

            void extractRenderState(GuiGraphicsExtractor graphics, Font font, int x1, int y1, int mouseX, int mouseY) {
                int x2 = x1 + 10;
                int y2 = y1 + 10;
                RenderUtils.blitFull(graphics, this.icon, x1 + 1, y1 + 1, x2 - 1, y2 - 1);

                // hover info
                if (MathHelper.isWithinBounds(mouseX, mouseY, x1, y1, x2 - x1, y2 - y1)) {
                    graphics.setTooltipForNextFrame(font, this.tooltip, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }
}
