package tnt.tarkovcraft.core.util.helper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
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

    public static Vector2f getPosition(float x1, float y1, float x2, float y2, float width, float height, ScreenPositionCalculator horizontal, ScreenPositionCalculator vertical) {
        float x = horizontal.getPosition(x1, x2, width);
        float y = vertical.getPosition(y1, y2, height);
        return new Vector2f(x, y);
    }
}
