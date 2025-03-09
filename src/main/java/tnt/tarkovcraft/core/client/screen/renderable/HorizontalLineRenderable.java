package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;

public class HorizontalLineRenderable implements Renderable {

    private final int x1;
    private final int x2;
    private final int y;
    private final int color;

    public HorizontalLineRenderable(int x1, int x2, int y, int color) {
        this.x1 = x1;
        this.x2 = x2;
        this.y = y;
        this.color = color;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.hLine(this.x1, this.x2, this.y, this.color);
    }
}
