package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.Consumer;

public abstract class AbstractTextRenderable<T> implements Renderable {

    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected final int color;
    protected final boolean shadow;
    protected final Font font;
    protected final T text;

    public AbstractTextRenderable(int x, int y, int width, int height, int color, boolean shadow, Font font, T text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.shadow = shadow;
        this.font = font;
        this.text = text;
    }

    public abstract void renderText(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.x, this.y, this.x + this.width, this.y + this.height);
        this.renderText(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
    }

    public static class StringRenderable extends AbstractTextRenderable<String> {

        public StringRenderable(int x, int y, int width, int height, int color, boolean shadow, Font font, String text) {
            super(x, y, width, height, color, shadow, font, text);
        }

        @Override
        public void renderText(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            graphics.drawString(this.font, this.text, this.x, this.y, this.color, this.shadow);
        }
    }

    public static class CenteredStringRenderable extends AbstractTextRenderable<String> {

        public CenteredStringRenderable(int x, int y, int width, int height, int color, boolean shadow, Font font, String text) {
            super(x, y, width, height, color, shadow, font, text);
        }

        @Override
        public void renderText(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            float left = this.x + (this.width - this.font.width(this.text)) / 2.0F;
            float top = this.y + (this.height - this.font.lineHeight) / 2.0F;
            graphics.drawString(this.font, this.text, left, top, this.color, this.shadow);
        }
    }

    public static class ScrollingStringRenderable extends AbstractTextRenderable<String> {

        private final Component textComponent;

        public ScrollingStringRenderable(int x, int y, int width, int height, int color, boolean shadow, Font font, String text) {
            super(x, y, width, height, color, shadow, font, text);
            this.textComponent = Component.literal(text);
        }

        @Override
        public void renderText(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            int top = this.y + (this.height - this.font.lineHeight) / 2;
            graphics.drawScrollingString(this.font, this.textComponent, this.x, this.x + this.width, top, this.color);
        }
    }

    public static class FormattedSequenceRenderable extends AbstractTextRenderable<FormattedCharSequence> {

        public FormattedSequenceRenderable(int x, int y, int width, int height, int color, boolean shadow, Font font, FormattedCharSequence text) {
            super(x, y, width, height, color, shadow, font, text);
        }

        @Override
        public void renderText(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            graphics.drawString(this.font, this.text, this.x, this.y, this.color, this.shadow);
        }
    }

    public static class ComponentRenderable extends AbstractTextRenderable<Component> {

        public ComponentRenderable(int x, int y, int width, int height, int color, boolean shadow, Font font, Component text) {
            super(x, y, width, height, color, shadow, font, text);
        }

        @Override
        public void renderText(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            graphics.drawString(this.font, this.text, this.x, this.y, this.color, this.shadow);
        }
    }
}
