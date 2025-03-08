package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class LayeredScreen extends Screen {

    protected final Screen parent;

    public LayeredScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if (this.displayParentOnClose())
            this.minecraft.setScreen(this.parent);
    }

    public boolean displayParentOnClose() {
        return true;
    }
}
