package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;

public class ScrollbarWidget extends AbstractWidget {

    private final Scrollable scrollable;
    private int backgroundColor = 0xFF << 24;
    private int foregroundColor = 0xFFFFFFFF;
    private int scrollBarMargin = 1;
    private boolean alwaysVisible;

    public ScrollbarWidget(int x, int y, int width, int height, Scrollable scrollable) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.scrollable = scrollable;
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

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.backgroundColor);
        // calculate scrollbar positions
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
