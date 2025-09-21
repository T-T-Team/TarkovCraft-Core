package tnt.tarkovcraft.core.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.event.client.AddPlayerProfileLabelsEvent;
import tnt.tarkovcraft.core.client.IconWithLabel;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.renderable.*;
import tnt.tarkovcraft.core.client.screen.widget.EntityWidget;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.client.util.PlayerProfileLabelContainer;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.init.CoreStatistics;
import tnt.tarkovcraft.core.common.statistic.DisplayStatistic;
import tnt.tarkovcraft.core.common.statistic.StatisticReader;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.common.weight.WeightSystem;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.helper.RenderUtils;
import tnt.tarkovcraft.core.util.helper.TextHelper;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class StatisticsScreen extends CharacterSubScreen {

    public static final Component OVERVIEW_LABEL = TextHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "statistics", "overview").withStyle(ChatFormatting.BOLD);
    public static final ResourceLocation ICON_KILLS = TarkovCraftCore.createResourceLocation("textures/icons/profile/kills.png");
    public static final ResourceLocation ICON_DEATHS = TarkovCraftCore.createResourceLocation("textures/icons/profile/deaths.png");
    public static final ResourceLocation ICON_WEIGHT = TarkovCraftCore.createResourceLocation("textures/icons/profile/weight.png");
    private double textScroll;

    public StatisticsScreen(Screen parent, UUID userId) {
        super(userId, CoreNavigators.STATISTICS_ENTRY);
    }

    @Override
    protected void init() {
        super.init();

        this.getPlayer().ifPresent(player -> {
            int left = this.width / 3;
            EntityWidget entityWidget = this.addRenderableWidget(new EntityWidget(0, 25, left, this.height - 25, player));
            entityWidget.setBackground(ColorPalette.BG_TRANSPARENT_WEAK);
            entityWidget.setOffset(0.0F, 0.75F, 0.0F);

            StatisticTracker tracker = player.getData(CoreDataAttachments.STATISTICS);

            RegistryAccess access = player.level().registryAccess();
            Registry<DisplayStatistic> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.DISPLAY_STATISTIC);
            List<DisplayStatistic> statistics = registry.listElements()
                    .map(Holder.Reference::value)
                    .sorted(Comparator.comparingInt(DisplayStatistic::getOrder))
                    .toList();

            this.addRenderableOnly(new ShapeRenderable(left, 25, this.width - left, 10, ColorPalette.BG_TRANSPARENT_WEAK));
            this.addRenderableOnly(new HorizontalLineRenderable(left, this.width, 35, ColorPalette.WHITE));
            this.addRenderableOnly(new VerticalLineRenderable(left - 1, 25, this.height, ColorPalette.WHITE));
            this.addRenderableOnly(new AbstractTextRenderable.Component(left + 3, 26, this.width - left, 10, ColorPalette.WHITE, true, this.font, OVERVIEW_LABEL));

            PlayerProfileLabelContainer container = this.getProfileLabels(player, tracker);
            List<PlayerProfileLabelContainer.ProfileLabelRow> rows = container.getRows();
            int top = this.height - rows.size() * 12;
            int gridWidth = (left - 9) / 3;
            for (int i = 0; i < rows.size(); i++) {
                PlayerProfileLabelContainer.ProfileLabelRow row = rows.get(i);
                int y = top + i * 12;
                if (row.left() != null) {
                    this.addRenderableOnly(new IconWithLabelRenderable(this.font, 3, y, gridWidth, 10, HorizontalAlignment.LEFT, row.left()));
                }
                if (row.center() != null) {
                    this.addRenderableOnly(new IconWithLabelRenderable(this.font, 3 + gridWidth, y, gridWidth, 10, HorizontalAlignment.CENTER, row.center()));
                }
                if (row.right() != null) {
                    this.addRenderableOnly(new IconWithLabelRenderable(this.font, 3 + 2 * gridWidth, y, gridWidth, 10, HorizontalAlignment.RIGHT, row.right()));
                }
            }

            ListWidget<TextStatisticWidget> textStats = this.addRenderableWidget(new ListWidget<>(left, 36, this.width - left, this.height - 26, statistics, (it, in) -> this.createTextStatistic(left, this.width - left, tracker, it, in)));
            textStats.setBackgroundColor(ColorPalette.BG_TRANSPARENT_WEAK);
            textStats.setScroll(this.textScroll);
            textStats.setScrollListener((x, y) -> this.textScroll = y);
        });

        this.initNotificationLayer();
    }

    private PlayerProfileLabelContainer getProfileLabels(Player player, StatisticTracker tracker) {
        PlayerProfileLabelContainer container = new PlayerProfileLabelContainer();
        // playername
        Component playerNameLabel = player.getDisplayName().copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD);
        container.addRow(PlayerProfileLabelContainer.ProfileLabelRow.center(PlayerProfileLabelContainer.ROW_PLAYER_NAME, new IconWithLabel(null, playerNameLabel)));
        container.addEmptyRow(PlayerProfileLabelContainer.ROW_STAT_SEPARATOR);
        // Stat row
        // kills
        long kills = tracker.get(CoreStatistics.KILLS.value());
        Component killLabel = Component.literal(String.valueOf(kills));
        Component weightLabel = null;
        // deaths
        long deaths = tracker.get(CoreStatistics.DEATHS.value());
        Component deathLabel = Component.literal(String.valueOf(deaths));
        // weight
        int iconColor = 0xFFAAAAAA;
        int textColor = 0xFFFFFF55;
        int weightColor = textColor;
        if (WeightSystem.isEnabled() && this.isMyProfile) {
            int weight = WeightSystem.getWeight(player);
            boolean overweight = WeightSystem.isOverweight(player);
            float overweightFactor = WeightSystem.getOverweightEffectFactor(player);
            weightLabel = WeightSystem.getWeightValueDisplay(weight, WeightSystem.NO_STYLE);
            weightColor = overweight ? (overweightFactor >= 1.0F ? 0xFFFF5555 : textColor) : 0xFF55FF55;
        }
        container.addRow(new PlayerProfileLabelContainer.ProfileLabelRow(
                PlayerProfileLabelContainer.ROW_STAT,
                new IconWithLabel(ICON_KILLS, killLabel, iconColor, textColor),
                new IconWithLabel(ICON_DEATHS, deathLabel, iconColor, textColor),
                new IconWithLabel(ICON_WEIGHT, weightLabel, iconColor, weightColor)
        ));
        // API for custom player labels
        AddPlayerProfileLabelsEvent event = NeoForge.EVENT_BUS.post(new AddPlayerProfileLabelsEvent(player, container));
        return event.getContainer();
    }

    private TextStatisticWidget createTextStatistic(int left, int width, StatisticReader reader, DisplayStatistic stat, int index) {
        TextStatisticWidget widget = new TextStatisticWidget(left, index * 10, width, 10, this.font, reader, stat);
        widget.setBackground(index % 2 != 0 ? 0x22 << 24 : 0x44 << 24);
        return widget;
    }

    public static final class TextStatisticWidget extends AbstractWidget {

        private final Font font;
        private final StatisticReader reader;
        private final DisplayStatistic statistic;

        private int background;

        public TextStatisticWidget(int x, int y, int width, int height, Font font, StatisticReader reader, DisplayStatistic statistic) {
            super(x, y, width, height, statistic.getLabel());
            this.font = font;
            this.reader = reader;
            this.statistic = statistic;
        }

        public void setBackground(int background) {
            this.background = background;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            if (RenderUtils.isVisibleColor(this.background)) {
                graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.background);
            }
            graphics.drawString(this.font, this.getMessage(), this.getX() + 3, this.getY() + 1, ColorPalette.TEXT_COLOR, true);
            String value = this.statistic.get(this.reader);
            graphics.drawString(this.font, value, this.getRight() - this.font.width(value) - 3, this.getY() + 1, ColorPalette.YELLOW, true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
