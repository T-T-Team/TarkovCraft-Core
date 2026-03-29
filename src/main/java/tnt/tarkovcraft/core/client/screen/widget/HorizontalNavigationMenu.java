package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import tnt.tarkovcraft.core.client.screen.navigation.NavigationEntry;
import tnt.tarkovcraft.core.client.screen.navigation.NavigationProvider;

import java.util.List;
import java.util.UUID;

public class HorizontalNavigationMenu<W extends AbstractWidget> extends AbstractWidget implements Scrollable {

    private final List<W> navigationMenuEntries;
    private double pageOffset;

    public HorizontalNavigationMenu(int x, int y, int width, int height, Screen parent, UUID userId, List<NavigationEntry> navigationMenuEntries, EntryDisplayBuilder<W> builder) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.navigationMenuEntries = navigationMenuEntries.stream()
                .filter(e -> e.isAvailable(parent, userId))
                .map(e -> builder.build(e, parent, userId) )
                .toList();
    }

    public HorizontalNavigationMenu(int x, int y, int width, int height, Screen parent, UUID userId, NavigationProvider provider, EntryDisplayBuilder<W> builder) {
        this(x, y, width, height, parent, userId, provider.getNavigationEntries(), builder);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
        int offset = 0;
        for (W widget : this.navigationMenuEntries) {
            widget.setX(this.getX() + offset + 2 - (int) this.pageOffset);
            widget.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
            offset += widget.getWidth() + 2;
        }
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double previousScroll = this.pageOffset;
        this.pageOffset = Scrollable.scroll(this, scrollY);;
        return previousScroll != this.pageOffset;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (W widget : this.navigationMenuEntries) {
            if (widget.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public double getScroll() {
        return this.pageOffset;
    }

    @Override
    public void setScroll(double scroll) {
        this.pageOffset = scroll;
    }

    @Override
    public double getVisibleSize() {
        return 0;
    }

    @Override
    public double getMaxScroll() {
        double width = 0.0D;
        double lastWidth = 0.0D;
        for (W widget : this.navigationMenuEntries) {
            double widgetSize = widget.getWidth();
            width += widgetSize + 4;
            lastWidth = widgetSize;
        }
        return Math.max(0.0D, width - 4 - lastWidth - this.width);
    }

    @Override
    public double getTotalSize() {
        return Math.max(0.0, this.navigationMenuEntries.stream().mapToInt(w -> w.getWidth() + 4).sum() - 4);
    }

    @FunctionalInterface
    public interface EntryDisplayBuilder<W extends AbstractWidget> {
        W build(NavigationEntry entry, Screen parent, UUID userId);
    }
}
