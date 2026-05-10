package tnt.tarkovcraft.core.client.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.Objects;
import java.util.function.Supplier;

public record IconWithLabel(Identifier icon, Supplier<Component> label, int iconColor, int labelColor) {

    public IconWithLabel(Identifier icon, Supplier<Component> label, int color) {
        this(icon, label, color, color);
    }

    public IconWithLabel(Identifier icon, Supplier<Component> label) {
        this(icon, label, ColorPalette.WHITE);
    }

    public IconWithLabel(Identifier icon, Component label, int iconColor, int labelColor) {
        this(icon, () -> label, iconColor, labelColor);
    }

    public IconWithLabel(Identifier icon, Component label, int color) {
        this(icon, () -> label, color, color);
    }

    public IconWithLabel(Identifier icon, Component label) {
        this(icon, () -> label);
    }

    public Component getLabel() {
        return Objects.requireNonNull(this.label.get(), "Label cannot be null");
    }
}
