package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.client.screen.listener.SimpleClickListener;

public class IconButton extends AbstractButton {

    private ResourceLocation icon;
    private final SimpleClickListener onClick;
    private Integer hoverBackground = ColorPalette.BG_HOVER_LIGHT;
    private int iconOffset = 0;
    private int tint = -1;

    public IconButton(int x, int y, int width, int height, ResourceLocation icon, SimpleClickListener onClick) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.icon = icon;
        this.onClick = onClick;
    }

    public IconButton(int x, int y, int width, int height, SimpleClickListener onClick) {
        this(x, y, width, height, null, onClick);
    }

    public void setHoverBackground(Integer hoverBackground) {
        this.hoverBackground = hoverBackground;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    public void setIconOffset(int iconOffset) {
        this.iconOffset = iconOffset;
    }

    public void setTint(int tint) {
        this.tint = tint;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float renderTick) {
        if (this.isHoveredOrFocused() && this.hoverBackground != null) {
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.hoverBackground);
        }
        if (this.icon != null) {
            graphics.innerBlit(
                    RenderType::guiTextured,
                    this.icon,
                    this.getX() + this.iconOffset,
                    this.getRight() - this.iconOffset,
                    this.getY() + this.iconOffset,
                    this.getBottom() - this.iconOffset,
                    0.0F, 1.0F,
                    0.0F, 1.0F,
                    this.tint
            );
        }
    }

    @Override
    public void onPress() {
        this.onClick.onClick();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
