package tnt.tarkovcraft.core.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.event.client.GetPlayerLabelsEvent;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.renderable.AbstractTextRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.HorizontalLineRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.ShapeRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.VerticalLineRenderable;
import tnt.tarkovcraft.core.client.screen.widget.EntityWidget;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.init.CoreStatistics;
import tnt.tarkovcraft.core.common.statistic.DisplayStatistic;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;
import tnt.tarkovcraft.core.util.context.ContextKeys;
import tnt.tarkovcraft.core.util.helper.RenderUtils;
import tnt.tarkovcraft.core.util.helper.TextHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StatisticsScreen extends CharacterSubScreen {

    public static final Component OVERVIEW_LABEL = TextHelper.createScreenComponent(TarkovCraftCore.MOD_ID, "statistics", "overview").withStyle(ChatFormatting.BOLD);
    private double textScroll;

    public StatisticsScreen(Context context) {
        super(context.getOrThrow(ContextKeys.UUID), CoreNavigators.STATISTICS_ENTRY);
    }

    @Override
    protected void init() {
        super.init();

        this.getPlayer().ifPresent(player -> {
            int left = this.width / 3;
            EntityWidget entityWidget = this.addRenderableWidget(new EntityWidget(0, 25, left, this.height - 25, player));
            entityWidget.setBackground(ColorPalette.BG_TRANSPARENT_WEAK);
            entityWidget.setOffset(0.0F, -0.4F, 0.0F);

            StatisticTracker tracker = player.getData(CoreDataAttachments.STATISTICS);

            RegistryAccess access = player.level().registryAccess();
            Registry<DisplayStatistic> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.DISPLAY_STATISTIC);
            List<DisplayStatistic> statistics = registry.listElements()
                    .map(Holder.Reference::value)
                    .sorted(Comparator.comparingInt(DisplayStatistic::getOrder))
                    .toList();

            Context context = ContextImpl.of(StatisticTracker.TRACKER, tracker);
            this.addRenderableOnly(new ShapeRenderable(left, 25, this.width - left, 10, ColorPalette.BG_TRANSPARENT_WEAK));
            this.addRenderableOnly(new HorizontalLineRenderable(left, this.width, 35, ColorPalette.WHITE));
            this.addRenderableOnly(new VerticalLineRenderable(left - 1, 25, this.height, ColorPalette.WHITE));
            this.addRenderableOnly(new AbstractTextRenderable.Component(left + 3, 26, this.width - left, 10, ColorPalette.WHITE, true, this.font, OVERVIEW_LABEL));

            List<Component> playerLabels = this.getPlayerLabels(player, tracker);
            int top = this.height - 20 - playerLabels.size() * 12;
            for (int i = 0; i < playerLabels.size(); i++) {
                Component playerLabel = playerLabels.get(i);
                this.addRenderableOnly(new AbstractTextRenderable.CenteredComponent(0, top + i * 12, left, 10, 0xFFFFFF, true, this.font, playerLabel));
            }

            ListWidget<TextStatisticWidget> textStats = this.addRenderableWidget(new ListWidget<>(left, 36, this.width - left, this.height - 26, statistics, (it, in) -> this.createTextStatistic(left, this.width - left, context, it, in)));
            textStats.setBackgroundColor(ColorPalette.BG_TRANSPARENT_WEAK);
            textStats.setScroll(this.textScroll);
            textStats.setScrollListener((x, y) -> this.textScroll = y);
        });
    }

    private List<Component> getPlayerLabels(Player player, StatisticTracker tracker) {
        List<Component> playerLabels = new ArrayList<>();
        // playername
        playerLabels.add(player.getDisplayName().copy().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
        // kdr
        long kills = tracker.get(CoreStatistics.PLAYER_KILLS.value());
        long deaths = Math.max(tracker.get(CoreStatistics.PLAYER_DEATHS.value()), 1L);
        float kdr = kills / (float) deaths;
        String kdrLabel = String.format(Locale.ROOT, "%.2f", kdr);
        MutableComponent kdrComponent = Component.literal(kdrLabel);
        playerLabels.add(Component.translatable("label.tarkovcraft_core.kdr", kdrComponent).withStyle(ChatFormatting.GRAY));
        // API for custom player labels
        GetPlayerLabelsEvent event = NeoForge.EVENT_BUS.post(new GetPlayerLabelsEvent(player, playerLabels));
        return event.getLabelList();
    }

    private TextStatisticWidget createTextStatistic(int left, int width, Context ctx, DisplayStatistic stat, int index) {
        TextStatisticWidget widget = new TextStatisticWidget(left, index * 10, width, 10, this.font, ctx, stat);
        widget.setBackground(index % 2 != 0 ? 0x22 << 24 : 0x44 << 24);
        return widget;
    }

    public static final class TextStatisticWidget extends AbstractWidget {

        private final Font font;
        private final Context context;
        private final DisplayStatistic statistic;

        private int background;

        public TextStatisticWidget(int x, int y, int width, int height, Font font, Context context, DisplayStatistic statistic) {
            super(x, y, width, height, statistic.getLabel());
            this.font = font;
            this.context = context;
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
            String value = this.statistic.get(this.context);
            graphics.drawString(this.font, value, this.getRight() - this.font.width(value) - 3, this.getY() + 1, ColorPalette.YELLOW, true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
