package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.client.IconWithLabel;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class IconWithLabelRenderable extends AbstractRenderable {

    private final Font font;
    private final IconWithLabel iconWithLabel;
    private final HorizontalAlignment alignment;

    private int iconSize;
    private int iconOffset = 2;
    private boolean textShadow = true;

    public IconWithLabelRenderable(Font font, int x, int y, int width, int height, HorizontalAlignment alignment, IconWithLabel iconWithLabel) {
        super(x, y, width, height);
        this.font = font;
        this.alignment = alignment;
        this.iconWithLabel = iconWithLabel;
        this.iconSize = this.iconWithLabel.icon() != null ? this.height : 0;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public void setIconOffset(int iconOffset) {
        this.iconOffset = iconOffset;
    }

    public void setTextShadow(boolean textShadow) {
        this.textShadow = textShadow;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation icon = this.iconWithLabel.icon();
        Component label = this.iconWithLabel.getLabel();
        int itemWidth = this.font.width(label);
        if (icon != null) {
            itemWidth += this.iconOffset + this.iconSize;
        }
        int left = (int) this.alignment.getPosition(this.getX(), this.getRight(), itemWidth);
        if (icon != null) {
            RenderUtils.blitFull(guiGraphics, icon, left, this.y, left + this.iconSize, this.y + this.iconSize, this.iconWithLabel.iconColor());
            left += this.iconSize + this.iconOffset;
        }

        guiGraphics.drawString(this.font, this.iconWithLabel.getLabel(), left, this.y - (this.height - this.font.lineHeight) / 2 + 1, this.iconWithLabel.labelColor(), this.textShadow);
    }
}
