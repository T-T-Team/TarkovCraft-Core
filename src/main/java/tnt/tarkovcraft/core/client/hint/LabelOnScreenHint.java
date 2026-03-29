package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.Objects;

public class LabelOnScreenHint implements OnScreenHint {

    private final Component label;

    public LabelOnScreenHint(Component label) {
        this.label = Objects.requireNonNull(label);
    }

    @Override
    public void extract(GuiGraphicsExtractor graphics, Font font, Window window, int x, int y, int width, int height, float delta) {
        graphics.text(font, this.label, x, y + this.getHeightOffset(), this.getTextColor(), this.enableTextShadow());
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
        return font.width(this.label);
    }

    @Override
    public int getHintHeight(Window window, Font font) {
        return 15;
    }

    protected boolean enableTextShadow() {
        return false;
    }

    protected int getTextColor() {
        return ColorPalette.WHITE;
    }

    protected int getHeightOffset() {
        return 3;
    }
}
