package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class LabelButton extends Button {

    private int backgroundColor = 0;
    private int backgroundDisabledColor = 0;
    private int backgroundHoverColor = ColorPalette.BG_HOVER_WEAK;
    private int color = ColorPalette.TEXT_COLOR;
    private int colorDisabled = ColorPalette.TEXT_COLOR_DISABLED;
    private int colorSelected = ColorPalette.WHITE;

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

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundDisabledColor(int backgroundDisabledColor) {
        this.backgroundDisabledColor = backgroundDisabledColor;
    }

    public void setBackgroundHoverColor(int backgroundHoverColor) {
        this.backgroundHoverColor = backgroundHoverColor;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float renderTick) {
        Font font = Minecraft.getInstance().font;
        int background = this.isActive() ? this.isHoveredOrFocused() ? this.backgroundHoverColor : this.backgroundColor : this.backgroundDisabledColor;
        if (RenderUtils.isNotTransparent(background)) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), background);
        }
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
