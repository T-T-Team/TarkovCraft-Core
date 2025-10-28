package tnt.tarkovcraft.core.util.helper;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import tnt.tarkovcraft.core.util.ScreenPositionCalculator;

import java.util.List;

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
                icon,
                x1, x2, y1, y2,
                0,
                0.0F, 1.0F,
                0.0F, 1.0F,
                ARGB.redFloat(color),
                ARGB.greenFloat(color),
                ARGB.blueFloat(color),
                ARGB.alphaFloat(color)
        );
    }

    public static void fill(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color) {
        fillGradient(graphics, x1, y1, x2, y2, color, color);
    }

    public static void fillGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom, int colorTo) {
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix4f = graphics.pose().last().pose();
        consumer.addVertex(matrix4f, x1, y1, 0).setColor(colorFrom);
        consumer.addVertex(matrix4f, x1, y2, 0).setColor(colorTo);
        consumer.addVertex(matrix4f, x2, y2, 0).setColor(colorTo);
        consumer.addVertex(matrix4f, x2, y1, 0).setColor(colorFrom);
        graphics.flushIfUnmanaged();
    }

    public static void fillDarkenGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom, float rgbScale) {
        fillGradient(graphics, x1, y1, x2, y2, colorFrom, ARGB.scaleRGB(colorFrom, rgbScale));
    }

    public static void fillDarkenGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom) {
        fillDarkenGradient(graphics, x1, y1, x2, y2, colorFrom, 0.8F);
    }

    public static Vector2f getPosition(float x1, float y1, float x2, float y2, float width, float height, ScreenPositionCalculator horizontal, ScreenPositionCalculator vertical) {
        float x = horizontal.getPosition(x1, x2, width);
        float y = vertical.getPosition(y1, y2, height);
        return new Vector2f(x, y);
    }

    public static List<FormattedCharSequence> splitTooltip(List<Component> lines, Font font) {
        return lines.stream().flatMap(text -> font.split(text, 170).stream()).toList();
    }
}
