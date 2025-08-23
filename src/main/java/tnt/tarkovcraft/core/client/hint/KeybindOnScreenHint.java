package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

public class KeybindOnScreenHint implements OnScreenHint {

    protected final KeyMapping bind;

    public KeybindOnScreenHint(KeyMapping bind) {
        this.bind = bind;
    }

    @Override
    public void render(GuiGraphics graphics, Font font, Window window, int x, int y, int width, int height, float delta) {
        Component eventLabel = this.bind.getDisplayName();
        Component keyLabel = this.bind.getTranslatedKeyMessage();

        int keyWidth = font.width(keyLabel);
        int bgHeight = height - 3;
        graphics.fill(x, y + 2, x + keyWidth + 5, y + 2 + bgHeight, ColorPalette.BLACK);
        graphics.fill(x + 1, y + 3, x + keyWidth + 4, y + 1 + bgHeight, ColorPalette.GOLD);
        graphics.drawString(font, keyLabel, x + 3, y + 5, this.bind.isDown() ? ColorPalette.WHITE : ColorPalette.BLACK, false);

        graphics.drawString(font, eventLabel, x + keyWidth + 9, y + 5, ColorPalette.WHITE, true);
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public boolean isAdvanced() {
        return false;
    }

    @Override
    public void onHintUpdate() {
    }

    @Override
    public int getHintWidth(Window window, Font font) {
        int key = font.width(this.bind.getTranslatedKeyMessage());
        int name = font.width(this.bind.getDisplayName());
        return key + 9 + name;
    }

    @Override
    public int getHintHeight(Window window, Font font) {
        return 16;
    }
}
