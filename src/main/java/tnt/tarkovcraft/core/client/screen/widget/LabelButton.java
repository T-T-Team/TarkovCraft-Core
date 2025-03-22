package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

public class LabelButton extends Button {

    private int backgroundHoverColor = ColorPalette.BG_HOVER_LIGHT;
    private int color = 0xFFCCCCCC;
    private int colorDisabled = 0xFF666666;
    private int colorSelected = 0xFFFFFFFF;

    public LabelButton(Builder builder) {
        super(builder);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColorDisabled(int colorDisabled) {
        this.colorDisabled = colorDisabled;
    }

    public void setColorSelected(int colorSelected) {
        this.colorSelected = colorSelected;
    }

    public void setBackgroundHoverColor(int backgroundHoverColor) {
        this.backgroundHoverColor = backgroundHoverColor;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float renderTick) {
        Font font = Minecraft.getInstance().font;
        if (this.isHoveredOrFocused())
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.backgroundHoverColor);
        int color = this.isActive() ? this.isHoveredOrFocused() ? this.colorSelected : this.color : this.colorDisabled;
        guiGraphics.drawString(
                font,
                this.getMessage().getString(),
                this.getX() + (this.getWidth() - font.width(this.getMessage())) / 2.0F,
                this.getY() + (this.getHeight() - font.lineHeight) / 2.0F,
                color,
                false
        );
    }
}
