package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.GuiGraphics;

public class ShapeRenderable extends AbstractRenderable {

    protected final int color;

    public ShapeRenderable(int x, int y, int width, int height, int color) {
        super(x, y, width, height);
        this.color = color;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.fill(this.x, this.y, this.getRight(), this.getBottom(), this.color);
    }
}
