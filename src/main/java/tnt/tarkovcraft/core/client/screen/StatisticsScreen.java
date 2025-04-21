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
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.screen.navigation.CoreNavigators;
import tnt.tarkovcraft.core.client.screen.renderable.AbstractTextRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.HorizontalLineRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.ShapeRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.VerticalLineRenderable;
import tnt.tarkovcraft.core.client.screen.widget.EntityWidget;
import tnt.tarkovcraft.core.client.screen.widget.ListWidget;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.statistic.DisplayStatistic;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;
import tnt.tarkovcraft.core.util.context.ContextKeys;
import tnt.tarkovcraft.core.util.helper.TextHelper;

import java.util.Comparator;
import java.util.List;

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

            ListWidget<TextStatisticWidget> textStats = this.addRenderableWidget(new ListWidget<>(left, 36, this.width - left, this.height - 26, statistics, (it, in) -> this.createTextStatistic(left, this.width - left, context, it, in)));
            textStats.setBackgroundColor(ColorPalette.BG_TRANSPARENT_NORMAL);
            textStats.setScroll(this.textScroll);
            textStats.setScrollListener((x, y) -> this.textScroll = y);
            textStats.setAdditionalItemSpacing(1);
        });
    }


    private TextStatisticWidget createTextStatistic(int left, int width, Context ctx, DisplayStatistic stat, int index) {
        return new TextStatisticWidget(left, index * 11, width, 10, this.font, ctx, stat);
    }

    public static final class TextStatisticWidget extends AbstractWidget {

        private final Font font;
        private final Context context;
        private final DisplayStatistic statistic;

        public TextStatisticWidget(int x, int y, int width, int height, Font font, Context context, DisplayStatistic statistic) {
            super(x, y, width, height, statistic.getLabel());
            this.font = font;
            this.context = context;
            this.statistic = statistic;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), ColorPalette.BG_TRANSPARENT_WEAK);
            graphics.drawString(this.font, this.getMessage(), this.getX() + 3, this.getY() + 1, ColorPalette.TEXT_COLOR, true);
            String value = this.statistic.get(this.context);
            graphics.drawString(this.font, value, this.getRight() - this.font.width(value) - 3, this.getY() + 1, ColorPalette.YELLOW, true);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        }
    }
}
