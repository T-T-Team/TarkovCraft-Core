package tnt.tarkovcraft.core.api.event.client;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.client.util.PlayerProfileLabelContainer;

public class AddPlayerProfileLabelsEvent extends Event {

    private final Player player;
    private final PlayerProfileLabelContainer container;

    public AddPlayerProfileLabelsEvent(Player player, PlayerProfileLabelContainer container) {
        this.player = player;
        this.container = container;
    }

    public PlayerProfileLabelContainer getContainer() {
        return container;
    }

    public Player getPlayer() {
        return player;
    }
}
