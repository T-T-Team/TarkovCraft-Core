package tnt.tarkovcraft.core.client.screen.renderable;

import net.minecraft.client.gui.components.Renderable;

public abstract class AbstractRenderable implements Renderable {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public AbstractRenderable(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRight() {
        return this.getX() + this.getWidth();
    }

    public int getBottom() {
        return this.getY() + this.getHeight();
    }
}
