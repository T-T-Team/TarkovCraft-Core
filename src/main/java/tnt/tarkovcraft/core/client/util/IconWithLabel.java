package tnt.tarkovcraft.core.client.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.function.Supplier;

public record IconWithLabel(Identifier icon, Supplier<Component> label, int iconColor, int labelColor) {

    public static final Component MISSING_LABEL = Component.translatable("label.tarkovcraft_core.missing_text").withStyle(ChatFormatting.RED);

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
        Component label = this.label.get();
        return label != null
                ? label
                : MISSING_LABEL;
    }
}
