package tnt.tarkovcraft.core.client.screen;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface DataScreen {

    // TODO should be called when player profile data are sent to client
    default void onPlayerProfileDataReceived(Player player) {
    }
}
