package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;

public class VerticalLineRenderable implements Renderable {

    private final int x;
    private final int minY;
    private final int maxY;
    private final int color;

    public VerticalLineRenderable(int x, int minY, int maxY, int color) {
        this.x = x;
        this.minY = minY;
        this.maxY = maxY;
        this.color = color;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int i1, float v) {
        guiGraphics.verticalLine(this.x, this.minY, this.maxY, this.color);
    }
}
