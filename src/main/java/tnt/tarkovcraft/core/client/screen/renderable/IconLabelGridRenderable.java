package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import tnt.tarkovcraft.core.client.util.IconWithLabel;
import tnt.tarkovcraft.core.util.HorizontalAlignment;

import java.util.ArrayList;
import java.util.List;

public class IconLabelGridRenderable implements Renderable {

    private final int x;
    private final int y;
    private final int width;
    private int margin;

    private final List<IconWithLabelRenderable> left;
    private final List<IconWithLabelRenderable> center;
    private final List<IconWithLabelRenderable> right;

    public IconLabelGridRenderable(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.left = new ArrayList<>();
        this.center = new ArrayList<>();
        this.right = new ArrayList<>();
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public List<IconWithLabelRenderable> getContainer(HorizontalAlignment alignment) {
        return switch (alignment) {
            case LEFT -> this.left;
            case CENTER -> this.center;
            case RIGHT -> this.right;
        };
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {

    }

    @FunctionalInterface
    public interface Builder {
        IconWithLabelRenderable build(int left, int top, int gridWidth, int gridHeight, IconWithLabel entry);
    }
}
