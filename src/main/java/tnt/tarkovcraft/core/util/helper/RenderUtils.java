package tnt.tarkovcraft.core.util.helper;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import tnt.tarkovcraft.core.client.screen.state.ColoredRectangleRenderState;
import tnt.tarkovcraft.core.util.ScreenPositionCalculator;

public final class RenderUtils {

    public static boolean isVisibleColor(int color) {
        return isNotTransparent(color, 0);
    }

    public static boolean isNotTransparent(int color, int limit) {
        int a = ARGB.alpha(color);
        return a > limit;
    }

    public static void blitFull(GuiGraphics graphics, Identifier icon, int x1, int y1, int x2, int y2) {
        blitFull(graphics, icon, x1, y1, x2, y2, -1);
    }

    public static void blitFull(GuiGraphics graphics, Identifier icon, int x1, int y1, int x2, int y2, int color) {
        graphics.innerBlit(
                RenderPipelines.GUI_TEXTURED,
                icon,
                x1, x2, y1, y2,
                0.0F, 1.0F,
                0.0F, 1.0F,
                color
        );
    }

    public static void fill(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color) {
        fillGradient(graphics, x1, y1, x2, y2, color, color);
    }

    public static void fillGradient(GuiGraphics graphics, float x1, float y1, float x2, float y2, int colorFrom, int colorTo) {
        Matrix3x2f pose = new Matrix3x2f(graphics.pose());
        ScreenRectangle scissor = graphics.peekScissorStack();
        ScreenRectangle bounds = getBounds(pose, scissor, x1, y1, x2, y2);
        graphics.submitGuiElementRenderState(new ColoredRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x1, y1, x2, y2, colorFrom, colorTo, scissor, bounds));
    }

    public static ScreenRectangle getBounds(Matrix3x2f pose, @Nullable ScreenRectangle scissor, float x1, float y1, float x2, float y2) {
        ScreenRectangle screenrectangle = new ScreenRectangle(Mth.floor(x1), Mth.floor(y1), Mth.ceil(x2 - x1), Mth.ceil(y2 - y1)).transformMaxBounds(pose);
        return scissor != null ? scissor.intersection(screenrectangle) : screenrectangle;
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

    public static void drawScrollingString(Component text, ActiveTextCollector collector, int left, int right, int top, int bottom, int color) {
        if (isVisibleColor(color))
            text = text.copy().withStyle((style) -> style.withColor(color));
        collector.acceptScrolling(text, left, left, right, top, bottom, collector.defaultParameters());
    }
}
