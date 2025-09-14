package tnt.tarkovcraft.core.client;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.client.screen.ColorPalette;

import java.util.function.Supplier;

public record IconWithLabel(ResourceLocation icon, Supplier<Component> label, int iconColor, int labelColor) {

    public IconWithLabel(ResourceLocation icon, Supplier<Component> label, int color) {
        this(icon, label, color, color);
    }

    public IconWithLabel(ResourceLocation icon, Supplier<Component> label) {
        this(icon, label, ColorPalette.WHITE);
    }

    public IconWithLabel(ResourceLocation icon, Component label, int iconColor, int labelColor) {
        this(icon, () -> label, iconColor, labelColor);
    }

    public IconWithLabel(ResourceLocation icon, Component label, int color) {
        this(icon, () -> label, color, color);
    }

    public IconWithLabel(ResourceLocation icon, Component label) {
        this(icon, () -> label);
    }

    public Component getLabel() {
        return label.get();
    }
}
