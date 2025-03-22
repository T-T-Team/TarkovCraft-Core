package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.GuiGraphics;

public class GradientShapeRenderable extends ShapeRenderable {

    private final int color2;

    public GradientShapeRenderable(int x, int y, int width, int height, int color, int color2) {
        super(x, y, width, height, color);
        this.color2 = color2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.fillGradient(this.x, this.y, this.getRight(), this.getBottom(), this.color, this.color2);
    }
}
