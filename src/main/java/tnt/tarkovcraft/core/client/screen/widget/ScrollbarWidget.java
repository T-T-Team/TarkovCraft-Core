package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class ScrollbarWidget extends AbstractWidget {

    private final Scrollable scrollable;
    private int background;
    private int backgroundColor = 0xFF << 24;
    private int foregroundColor = 0xFFFFFFFF;
    private int scrollBarMargin = 1;
    private boolean alwaysVisible;
    private boolean clickable = true;

    public ScrollbarWidget(int x, int y, int width, int height, Scrollable scrollable) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.scrollable = scrollable;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setForegroundColor(int foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setScrollBarMargin(int scrollBarMargin) {
        this.scrollBarMargin = scrollBarMargin;
    }

    public void setAlwaysVisible(boolean alwaysVisible) {
        this.alwaysVisible = alwaysVisible;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.canRender()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.backgroundColor);
            double max = this.scrollable.getMaxScroll();
            double sum = this.scrollable.getTotalSize();
            double amountMin = max > 0 ? this.scrollable.getScroll() / sum : 0.0;
            double amountMax = max > 0 ? (this.scrollable.getScroll() + this.scrollable.getVisibleSize()) / sum : 1.0;
            int y1 = this.getY() + (int) (amountMin * this.height);
            int y2 = this.getY() + (int) (amountMax * this.height);
            guiGraphics.fill(this.getX() + this.scrollBarMargin, y1 + this.scrollBarMargin, this.getRight() - this.scrollBarMargin, y2 - this.scrollBarMargin, this.foregroundColor);
        } else if (RenderUtils.isNotTransparent(this.background)) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.background);
        }
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return this.canRender() && this.clickable;
    }

    protected boolean canRender() {
        return this.alwaysVisible || this.scrollable.getMaxScroll() > 0;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        double relativeY = (mouseY - this.getY()) / this.getHeight();
        double max = this.scrollable.getMaxScroll();
        double scrollAmount = max * relativeY;
        this.scrollable.setScroll(scrollAmount);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.onClick(mouseX, mouseY, 0);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
