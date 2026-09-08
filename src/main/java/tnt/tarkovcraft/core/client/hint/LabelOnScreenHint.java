package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHint;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHintRenderer;
import tnt.tarkovcraft.core.api.client.hint.TextHint;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.Objects;

public class LabelOnScreenHint extends AbstractOnScreenHint implements TextHint {

    private final Component label;
    private final TextOptions options;

    public LabelOnScreenHint(Component label) {
        this(label, TextOptions.DEFAULT);
    }

    public LabelOnScreenHint(Component label, TextOptions options) {
        this.label = Objects.requireNonNull(label);
        this.options = Objects.requireNonNull(options);
    }

    @Override
    public OnScreenHintRenderer<?> createRenderer() {
        return new Renderer<>(this.options);
    }

    @Override
    public Component text() {
        return this.label;
    }

    public static final class Renderer<T extends OnScreenHint & TextHint> implements OnScreenHintRenderer<T> {

        private final TextOptions options;

        public Renderer() {
            this(TextOptions.DEFAULT);
        }

        public Renderer(TextOptions options) {
            this.options = options;
        }

        @Override
        public void extract(T hint, GuiGraphicsExtractor graphics, Window window, Font font, int x, int y, float delta) {
            Component text = hint.text();
            graphics.text(font, text, x, y, this.options.color, this.options.shadow);
        }

        @Override
        public int width(T hint, Window window, Font font) {
            return font.width(hint.text());
        }

        @Override
        public int height(T hint, Window window, Font font) {
            return 10;
        }
    }

    public record TextOptions(int color, boolean shadow) {
        public static final TextOptions DEFAULT = new TextOptions(ColorPalette.WHITE, false);
    }
}
