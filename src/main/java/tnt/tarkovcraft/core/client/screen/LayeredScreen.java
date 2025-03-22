package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.client.screen.renderable.NotificationScreen;

public abstract class LayeredScreen extends NotificationScreen {

    protected final Screen parent;

    public LayeredScreen(Component title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.parent.init(this.minecraft, this.width, this.height);
    }

    @Override
    public void onClose() {
        if (this.displayParentOnClose())
            this.openParentScreen();
    }

    public void openParentScreen() {
        this.minecraft.setScreen(this.parent);
    }

    public boolean displayParentOnClose() {
        return true;
    }


}
