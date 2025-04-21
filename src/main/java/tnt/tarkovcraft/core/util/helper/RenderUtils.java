package tnt.tarkovcraft.core.util.helper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

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
}
