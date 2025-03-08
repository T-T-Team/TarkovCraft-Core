package tnt.tarkovcraft.core.util;

import net.minecraft.network.chat.Component;

public class LocalizationHelper {

    public static String createKeybindName(String owner, String name) {
        return "key." + owner + "." + name;
    }

    public static Component createScreenTitle(String owner, String screen) {
        return createScreenComponent(owner, screen, "title");
    }

    public static Component createScreenComponent(String owner, String screen, String componentType) {
        return Component.translatable("screen." + owner + "." + screen + "." + componentType);
    }
}
