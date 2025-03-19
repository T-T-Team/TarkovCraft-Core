package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.client.screen.ColorPalette;
import tnt.tarkovcraft.core.client.screen.listener.SimpleClickListener;

import java.util.function.BiConsumer;

public class IconButton extends AbstractButton {

    private ResourceLocation icon;
    private final SimpleClickListener onClick;
    private final BiConsumer<GuiGraphics, ResourceLocation> renderer;
    private Integer hoverBackground = ColorPalette.BG_HOVER_LIGHT;

    public IconButton(int x, int y, int width, int height, ResourceLocation icon, SimpleClickListener onClick, BiConsumer<GuiGraphics, ResourceLocation> renderer) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.icon = icon;
        this.onClick = onClick;
        this.renderer = renderer;
    }

    public IconButton(int x, int y, int width, int height, SimpleClickListener onClick, BiConsumer<GuiGraphics, ResourceLocation> renderer) {
        this(x, y, width, height, null, onClick, renderer);
    }

    public void setHoverBackground(Integer hoverBackground) {
        this.hoverBackground = hoverBackground;
    }

    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float renderTick) {
        if (this.hoverBackground != null) {
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.hoverBackground);
        }
        if (this.icon != null)
            this.renderer.accept(graphics, this.icon);
    }

    @Override
    public void onPress() {
        this.onClick.onClick();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
