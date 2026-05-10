package tnt.tarkovcraft.core.api.event.client;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import tnt.tarkovcraft.core.api.client.LabelContainer;

public class AddPlayerProfileLabelsEvent extends Event {

    private final Player player;
    private final LabelContainer container;

    public AddPlayerProfileLabelsEvent(Player player, LabelContainer container) {
        this.player = player;
        this.container = container;
    }

    public LabelContainer getContainer() {
        return container;
    }

    public Player getPlayer() {
        return player;
    }
}
