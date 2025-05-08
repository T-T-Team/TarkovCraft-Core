package tnt.tarkovcraft.core.common.data.duration;

import net.minecraft.network.chat.Component;

public interface Unit {

    String getShortName();

    Component getLocalizedName(String value);

    int value();
}
