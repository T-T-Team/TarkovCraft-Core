package tnt.tarkovcraft.core.util.helper;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LocalizationHelper {

    public static String createKeybindName(String owner, String name) {
        return "key." + owner + "." + name;
    }

    public static MutableComponent createScreenTitle(String owner, String screen) {
        return createScreenComponent(owner, screen, "title");
    }

    public static MutableComponent createScreenComponent(String owner, String screen, String componentType) {
        return Component.translatable("screen." + owner + "." + screen + "." + componentType);
    }
}
