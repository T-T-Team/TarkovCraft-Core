package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class OverlayScreen extends LayeredScreen {

    public OverlayScreen(Component title, Screen parent) {
        super(title, parent);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.parent.render(guiGraphics, -1, -1, partialTick);
    }
}
