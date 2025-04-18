package tnt.tarkovcraft.core.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.context.ContextKey;
import tnt.tarkovcraft.core.TarkovCraftCore;

public final class ClientContextKeys {

    public static final ContextKey<Screen> PARENT_SCREEN = new ContextKey<>(TarkovCraftCore.createResourceLocation("parent_screen"));
}
