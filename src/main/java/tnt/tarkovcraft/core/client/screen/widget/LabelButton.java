package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;

public class LabelButton extends Button {

    private int color;
    private int colorDisabled;
    private int colorSelected;

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

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float renderTick) {
        Font font = Minecraft.getInstance().font;
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
