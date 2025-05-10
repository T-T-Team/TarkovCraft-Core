package tnt.tarkovcraft.core.util.helper;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import tnt.tarkovcraft.core.util.ScreenPositionCalculator;

public final class RenderUtils {

    public static boolean isVisibleColor(int color) {
        return isNotTransparent(color, 0);
    }

    public static boolean isNotTransparent(int color, int limit) {
        int a = ARGB.alpha(color);
        return a > limit;
    }

    public static void blitFull(GuiGraphics graphics, ResourceLocation icon, int x1, int y1, int x2, int y2) {
        blitFull(graphics, icon, x1, y1, x2, y2, -1);
    }

    public static void blitFull(GuiGraphics graphics, ResourceLocation icon, int x1, int y1, int x2, int y2, int color) {
        graphics.innerBlit(
                RenderType::guiTextured,
                icon,
                x1, x2, y1, y2,
                0.0F, 1.0F,
                0.0F, 1.0F,
                color
        );
    }

    public static void fill(GuiGraphics graphics, float x1, float y1, float x2, float y2, float z, int color) {
        Matrix4f pose = graphics.pose().last().pose();
        VertexConsumer consumer = graphics.bufferSource.getBuffer(RenderType.gui());
        consumer.addVertex(pose, x1, y1, z).setColor(color);
        consumer.addVertex(pose, x1, y2, z).setColor(color);
        consumer.addVertex(pose, x2, y2, z).setColor(color);
        consumer.addVertex(pose, x2, y1, z).setColor(color);
    }

    public static void fill(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color) {
        fill(graphics, x1, y1, x2, y2, 0, color);
    }

    public static void fillGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, float z, int colorFrom, int colorTo) {
        Matrix4f pose = graphics.pose().last().pose();
        VertexConsumer consumer = graphics.bufferSource.getBuffer(RenderType.gui());
        consumer.addVertex(pose, x1, y1, z).setColor(colorFrom);
        consumer.addVertex(pose, x1, y2, z).setColor(colorTo);
        consumer.addVertex(pose, x2, y2, z).setColor(colorTo);
        consumer.addVertex(pose, x2, y1, z).setColor(colorFrom);
    }

    public static void fillGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom, int colorTo) {
        fillGradient(graphics, x1, y1, x2, y2, 0, colorFrom, colorTo);
    }

    public static void fillDarkenGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, float z, int colorFrom, float rgbScale) {
        fillGradient(graphics, x1, y1, x2, y2, z, colorFrom, ARGB.scaleRGB(colorFrom, rgbScale));
    }

    public static void fillDarkenGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom, float rgbScale) {
        fillGradient(graphics, x1, y1, x2, y2, 0, colorFrom, ARGB.scaleRGB(colorFrom, rgbScale));
    }

    public static void fillDarkenGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, float z, int colorFrom) {
        fillDarkenGradient(graphics, x1, y1, x2, y2, z, colorFrom, 0.8F);
    }

    public static void fillDarkenGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom) {
        fillDarkenGradient(graphics, x1, y1, x2, y2, colorFrom, 0.8F);
    }

    public static Vector2f getPosition(float x1, float y1, float x2, float y2, float width, float height, ScreenPositionCalculator horizontal, ScreenPositionCalculator vertical) {
        float x = horizontal.getPosition(x1, x2, width);
        float y = vertical.getPosition(y1, y2, height);
        return new Vector2f(x, y);
    }
}
