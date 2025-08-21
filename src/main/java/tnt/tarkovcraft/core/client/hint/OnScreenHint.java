package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Register custom hints using {@link tnt.tarkovcraft.core.client.overlay.OnScreenHintLayer#register(OnScreenHint)}
 */
public interface OnScreenHint {

    void render(GuiGraphics graphics, Font font, Window window, int x, int y, int width, int height, float delta);

    void onHintUpdate();

    boolean isDisabled();

    boolean isAdvanced();

    int getHintWidth(Window window, Font font);

    int getHintHeight(Window window, Font font);
}
