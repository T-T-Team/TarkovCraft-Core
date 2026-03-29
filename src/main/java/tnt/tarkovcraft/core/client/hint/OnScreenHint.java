package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface OnScreenHint {

    void extract(GuiGraphicsExtractor graphics, Font font, Window window, int x, int y, int width, int height, float delta);

    void onHintUpdate();

    boolean isDisabled();

    boolean isAdvanced();

    int getHintWidth(Window window, Font font);

    int getHintHeight(Window window, Font font);
}
