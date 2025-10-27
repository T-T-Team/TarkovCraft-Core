package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.util.HorizontalAlignment;
import tnt.tarkovcraft.core.util.VerticalAlignment;

public class LabelRenderable extends AbstractRenderable {

    protected final Font font;
    protected final FormattedCharSequence text;
    protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
    protected VerticalAlignment verticalAlignment = VerticalAlignment.CENTER;
    protected int textColor = ColorPalette.TEXT_COLOR;
    protected boolean shadow = false;
    protected boolean scrolling = true;
    protected double scrollingSpeed = 1000.0;
    protected int xOffset;
    protected int yOffset;

    private LabelRenderable(int x, int y, int width, int height, Font font, FormattedCharSequence text) {
        super(x, y, width, height);
        this.font = font;
        this.text = text;
    }

    public static LabelRenderable fromString(int x, int y, int width, int height, Font font, String text) {
        return fromComponent(x, y, width, height, font, Component.literal(text));
    }

    public static LabelRenderable fromComponent(int x, int y, int width, int height, Font font, Component text) {
        return fromFormattedCharSequence(x, y, width, height, font, text.getVisualOrderText());
    }

    public static LabelRenderable fromFormattedCharSequence(int x, int y, int width, int height, Font font, FormattedCharSequence text) {
        return new LabelRenderable(x, y, width, height, font, text);
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

    public void setScrollingSpeed(double scrollingSpeed) {
        this.scrollingSpeed = scrollingSpeed;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int textWidth = this.font.width(this.text);
        int xText = Mth.floor(this.horizontalAlignment.getPosition(this.x, this.getRight(), textWidth));
        int yText = Mth.floor(this.verticalAlignment.getPosition(this.y, this.getBottom(), this.font.lineHeight));
        if (this.scrolling) {
            this.renderScrollingText(guiGraphics, xText, yText, textWidth);
        } else {
            this.renderStaticText(guiGraphics, xText, yText);
        }
    }

    protected void renderScrollingText(GuiGraphics graphics, int x, int y, int textWidth) {
        if (textWidth > this.width) {
            int overflow = textWidth - this.width;
            double timer = Util.getMillis() / this.scrollingSpeed;
            double d1 = Math.max(overflow * 0.5, 3.0);
            double d2 = Math.sin((Math.PI / 2) * Math.cos((Math.PI / 2) * timer / d1)) / 2.0 + 0.5;
            double d3 = Mth.lerp(d2, 0.0, overflow);
            this.startScissor(graphics);
            graphics.drawString(this.font, this.text, this.xOffset + this.x - (int) d3, this.yOffset + y, this.textColor, this.shadow);
            this.endScissor(graphics);
        } else {
            this.renderStaticText(graphics, x, y);
        }
    }

    protected void renderStaticText(GuiGraphics graphics, int x, int y) {
        graphics.drawString(this.font, this.text, this.xOffset + x, this.yOffset + y, this.textColor, this.shadow);
    }
}
