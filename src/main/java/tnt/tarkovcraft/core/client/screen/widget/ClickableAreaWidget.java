package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.CommonComponents;
import tnt.tarkovcraft.core.client.screen.listener.SimpleClickListener;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class ClickableAreaWidget extends AbstractWidget {

    private final SimpleClickListener clickListener;
    private int hoverColor;

    public ClickableAreaWidget(int x, int y, int width, int height, SimpleClickListener listener) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.clickListener = listener;
    }

    public void setHoverColor(Integer hoverColor) {
        this.hoverColor = hoverColor;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered && RenderUtils.isVisibleColor(this.hoverColor)) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.hoverColor);
        }
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return true;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        this.clickListener.onClick();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
