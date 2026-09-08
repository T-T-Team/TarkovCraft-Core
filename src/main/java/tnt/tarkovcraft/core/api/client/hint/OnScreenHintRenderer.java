package tnt.tarkovcraft.core.api.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public interface OnScreenHintRenderer<T extends OnScreenHint> {

    void extract(T hint, GuiGraphics graphics, Window window, Font font, int x, int y, float delta);

    int width(T hint, Window window, Font font);

    int height(T hint, Window window, Font font);
}
