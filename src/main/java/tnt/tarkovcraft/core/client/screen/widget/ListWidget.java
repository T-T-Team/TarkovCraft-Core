package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import tnt.tarkovcraft.core.client.screen.listener.ScrollChangeListener;
import tnt.tarkovcraft.core.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ListWidget<T extends AbstractWidget> extends AbstractWidget implements Scrollable {

    private final List<T> items;
    private double scroll;
    private ScrollChangeListener scrollListener;

    public <SRC> ListWidget(int x, int y, int width, int height, List<SRC> items, ListWidgetItemBuilder<T, SRC> builder) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.items = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            T item = builder.buildItem(items.get(i), i);
            this.items.add(item);
        }
    }

    public void setScrollListener(ScrollChangeListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
        boolean wasVisible = false;
        for (T item : this.getItems()) {
            if (!this.isItemVisible(item)) {
                if (wasVisible)
                    break;
                continue;
            }
            wasVisible = true;
            int oldY = item.getY();
            item.setY(oldY - (int) this.scroll);
            item.render(guiGraphics, mouseX, mouseY, partialTick);
            item.setY(oldY);
        }
        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        T item = this.getItemAt(mouseX, mouseY);
        if (item != null && item.isMouseOver(mouseX, mouseY) && item.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        double oldScroll = this.scroll;
        this.scroll = Scrollable.scroll(this, scrollY);
        if (this.scrollListener != null && oldScroll != this.scroll) {
            this.scrollListener.onScrollChange(scrollX, scrollY);
        }
        return oldScroll != this.scroll;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        T item = this.getItemAt(mouseX, mouseY);
        return item != null && item.mouseClicked(mouseX, mouseY, button);
    }

    public T getItemAt(double x, double y) {
        for (T item : this.getItems()) {
            if (item.isMouseOver(x, y - this.scroll)) {
                return item;
            }
        }
        return null;
    }

    public boolean isItemVisible(T item) {
        return MathHelper.areaOverlapsPartial(item.getX(), item.getY() - this.scroll, item.getWidth(), item.getHeight(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public List<T> getItems() {
        return this.items;
    }

    @Override
    public double getScroll() {
        return this.scroll;
    }

    @Override
    public double getMaxScroll() {
        int totalItemsHeight = this.getItems().stream()
                .mapToInt(AbstractWidget::getHeight)
                .sum();
        return totalItemsHeight - this.getVisibleSize();
    }

    @Override
    public void setScroll(double scroll) {
        this.scroll = scroll;
    }

    @Override
    public double getVisibleSize() {
        return this.height;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @FunctionalInterface
    public interface ListWidgetItemBuilder<T extends AbstractWidget, SRC> {
        T buildItem(SRC item, int index);
    }
}
