package tnt.tarkovcraft.core.client.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.client.LabelContainer;
import tnt.tarkovcraft.core.api.event.client.AddPlayerProfileLabelsEvent;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.renderable.*;
import tnt.tarkovcraft.core.client.screen.widget.EntityWidget;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.client.util.IconWithLabel;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class StatisticsScreen extends CharacterSubScreen {

    public static final Component OVERVIEW_LABEL = TextHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "statistics", "overview").withStyle(ChatFormatting.BOLD);
    public static final ResourceLocation ICON_KILLS = TarkovCraftCore.createIdentifier("textures/icons/profile/kills.png");
    public static final ResourceLocation ICON_DEATHS = TarkovCraftCore.createIdentifier("textures/icons/profile/deaths.png");
    public static final ResourceLocation ICON_WEIGHT = TarkovCraftCore.createIdentifier("textures/icons/profile/weight.png");
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
            HolderLookup.RegistryLookup<DisplayStatistic> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.DISPLAY_STATISTIC);
            List<DisplayStatistic> statistics = registry.listElements()
                    .map(Holder.Reference::value)
                    .sorted(Comparator.comparingInt(DisplayStatistic::getOrder))
                    .toList();

            this.addRenderableOnly(new ShapeRenderable(left, 25, this.width - left, 10, ColorPalette.BG_TRANSPARENT_WEAK));
            this.addRenderableOnly(new HorizontalLineRenderable(left, this.width, 35, ColorPalette.WHITE));
            this.addRenderableOnly(new VerticalLineRenderable(left - 1, 25, this.height, ColorPalette.WHITE));
            LabelRenderable overviewLabel = this.addRenderableOnly(LabelRenderable.fromComponent(left + 3, 26, this.width - left, 10, this.font, OVERVIEW_LABEL));
            overviewLabel.setTextColor(ColorPalette.WHITE);
            overviewLabel.setShadow(true);

            ProfileLabelContainer container = this.getProfileLabels(player, tracker);
            int top = this.height - container.getRows() * 12;
            container.compile(this.font, 0, top, left, 3)
                    .forEach(this::addRenderableOnly);

            Component playerName = player.getDisplayName().copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD);
            this.addRenderableOnly(new IconWithLabelRenderable(this.font, 0, top - 24, left, 12, HorizontalAlignment.CENTER, new IconWithLabel(null, playerName)));

            ListWidget<TextStatisticWidget> textStats = this.addRenderableWidget(new ListWidget<>(left, 36, this.width - left, this.height - 26, statistics, (it, in) -> this.createTextStatistic(left, this.width - left, tracker, it, in)));
            textStats.setBackgroundColor(ColorPalette.BG_TRANSPARENT_WEAK);
            textStats.setScroll(this.textScroll);
            textStats.setScrollListener((x, y) -> this.textScroll = y);
        });

        this.initNotificationLayer();
    }

    private ProfileLabelContainer getProfileLabels(Player player, StatisticTracker tracker) {
        ProfileLabelContainer container = new ProfileLabelContainer();
        int iconColor = 0xFFAAAAAA;
        int textColor = 0xFFFFFF55;
        // kills
        long kills = tracker.get(CoreStatistics.KILLS.value());
        Component killLabel = Component.literal(String.valueOf(kills));
        container.addLabel(HorizontalAlignment.LEFT, new IconWithLabel(ICON_KILLS, killLabel, iconColor, textColor));
        // deaths
        long deaths = tracker.get(CoreStatistics.DEATHS.value());
        Component deathLabel = Component.literal(String.valueOf(deaths));
        container.addLabel(HorizontalAlignment.CENTER, new IconWithLabel(ICON_DEATHS, deathLabel, iconColor, textColor));
        // weight
        if (WeightSystem.isEnabled() && this.isMyProfile) {
            int weight = WeightSystem.getWeight(player);
            boolean overweight = WeightSystem.isOverweight(player);
            float overweightFactor = WeightSystem.getOverweightEffectFactor(player);
            Component weightLabel = WeightSystem.getWeightValueDisplay(weight, WeightSystem.NO_STYLE);
            int weightColor = overweight ? (overweightFactor >= 1.0F ? 0xFFFF5555 : textColor) : 0xFF55FF55;
            container.addLabel(HorizontalAlignment.RIGHT, new IconWithLabel(ICON_WEIGHT, weightLabel, iconColor, weightColor));
        }
        // API for custom player labels
        NeoForge.EVENT_BUS.post(new AddPlayerProfileLabelsEvent(player, container));
        return container;
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

    private static final class ProfileLabelContainer implements LabelContainer {

        private final List<IconWithLabel> left;
        private final List<IconWithLabel> center;
        private final List<IconWithLabel> right;

        public ProfileLabelContainer() {
            this.left = new ArrayList<>();
            this.center = new ArrayList<>();
            this.right = new ArrayList<>();
        }

        @Override
        public List<IconWithLabel> getLabels(HorizontalAlignment alignment) {
            return ImmutableList.copyOf(this.get(alignment));
        }

        @Override
        public void removeLabelAt(HorizontalAlignment alignment, int index) {
            List<IconWithLabel> list = this.get(alignment);
            if (index >= 0 && index < list.size()) {
                list.remove(index);
            }
        }

        @Override
        public void addLabel(HorizontalAlignment alignment, IconWithLabel label) {
            List<IconWithLabel> list = this.get(alignment);
            list.add(label);
        }

        @Override
        public void addLabel(HorizontalAlignment alignment, IconWithLabel label, int index) {
            List<IconWithLabel> list = this.get(alignment);
            list.add(index, label);
        }

        public int getRows() {
            return Math.max(this.left.size(), Math.max(this.center.size(), this.right.size()));
        }

        public List<IconWithLabelRenderable> compile(Font font, int x, int y, int width, int margin) {
            int cellWidth = (width - 3 * margin) / 3;
            List<IconWithLabelRenderable> output = new ArrayList<>(this.left.size() + this.center.size() + this.right.size());
            output.addAll(this.compileColumn(font, this.left, HorizontalAlignment.LEFT, x + margin, y, cellWidth));
            output.addAll(this.compileColumn(font, this.center, HorizontalAlignment.CENTER, x + margin + cellWidth, y, cellWidth));
            output.addAll(this.compileColumn(font, this.right, HorizontalAlignment.RIGHT, x + margin + 2 * cellWidth, y, cellWidth));
            return output;
        }

        private List<IconWithLabelRenderable> compileColumn(Font font, List<IconWithLabel> column, HorizontalAlignment alignment, int x, int y, int cellWidth) {
            List<IconWithLabelRenderable> list = new ArrayList<>(column.size());
            for (int i = 0; i < column.size(); i++) {
                IconWithLabel label = column.get(i);
                IconWithLabelRenderable renderable = new IconWithLabelRenderable(font, x, y + i * 12, cellWidth, 10, alignment, label);
                list.add(renderable);
            }
            return list;
        }

        private List<IconWithLabel> get(HorizontalAlignment alignment) {
            return switch (alignment) {
                case LEFT -> this.left;
                case CENTER -> this.center;
                case RIGHT -> this.right;
            };
        }
    }
}
