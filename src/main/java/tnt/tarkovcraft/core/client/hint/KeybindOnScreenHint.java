package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHintRenderer;
import tnt.tarkovcraft.core.api.client.hint.TextHint;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

public class KeybindOnScreenHint extends AbstractOnScreenHint implements TextHint {

    protected final KeyMapping bind;
    private final Options options;

    public KeybindOnScreenHint(KeyMapping bind) {
        this(bind, Options.DEFAULT);
    }

    public KeybindOnScreenHint(KeyMapping bind, Options options) {
        this.bind = bind;
        this.options = options;
    }

    @Override
    public OnScreenHintRenderer<?> createRenderer() {
        return new Renderer(this.options);
    }

    @Override
    public Component text() {
        Component keyLabel = this.bind.getTranslatedKeyMessage();
        Component actionLabel = this.bind.getDisplayName();
        return Component.literal("[").append(keyLabel).append("] ").append(actionLabel);
    }

    public static final class Renderer implements OnScreenHintRenderer<KeybindOnScreenHint> {

        private static final int COMPONENT_HEIGHT = 16;
        private static final int BACKGROUND_HEIGHT = 13;

        private final Options options;

        public Renderer(Options options) {
            this.options = options;
        }

        @Override
        public void extract(KeybindOnScreenHint hint, GuiGraphicsExtractor graphics, Window window, Font font, int x, int y, float delta) {
            Component eventLabel = hint.bind.getDisplayName();
            Component keyLabel = hint.bind.getTranslatedKeyMessage();

            int bindTextWidth = font.width(keyLabel);
            // render badge
            graphics.fill(x, y + 2, x + bindTextWidth + 5, y + 2 + BACKGROUND_HEIGHT, this.options.badgeBorderColor);
            graphics.fill(x + 1, y + 3, x + bindTextWidth + 4, y + 1 + BACKGROUND_HEIGHT, this.options.badgeBgColor);
            // badge text
            graphics.text(font, keyLabel, x + 3, y + 5, hint.bind.isDown() ? this.options.badgeTextHighlightColor : this.options.badgeTextColor, this.options.badgeTextShadow);
            // action text
            graphics.text(font, eventLabel, x + bindTextWidth + 9, y + 5, hint.bind.isDown() ? this.options.actionTextHighlightColor : this.options.actionTextColor, this.options.actionTextShadow);
        }

        @Override
        public int width(KeybindOnScreenHint hint, Window window, Font font) {
            int key = font.width(hint.bind.getTranslatedKeyMessage());
            int name = font.width(hint.bind.getDisplayName());
            return key + 9 + name;
        }

        @Override
        public int height(KeybindOnScreenHint hint, Window window, Font font) {
            return COMPONENT_HEIGHT;
        }
    }

    public record Options(
            int badgeBgColor, int badgeBorderColor,
            int badgeTextColor, int badgeTextHighlightColor, boolean badgeTextShadow,
            int actionTextColor, int actionTextHighlightColor, boolean actionTextShadow
    ) {

        public static final Options DEFAULT = new Options(
                ColorPalette.GOLD, ColorPalette.BLACK,
                ColorPalette.BLACK, ColorPalette.WHITE, false,
                ColorPalette.WHITE, ColorPalette.WHITE, true
        );
    }
}
