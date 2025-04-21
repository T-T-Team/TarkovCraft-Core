package tnt.tarkovcraft.core.client.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

import java.util.List;

public class GetPlayerLabelsEvent extends Event {

    private final Player player;
    private final List<Component> labelList;

    public GetPlayerLabelsEvent(Player player, List<Component> labelList) {
        this.player = player;
        this.labelList = labelList;
    }

    public List<Component> getLabelList() {
        return labelList;
    }

    public Player getPlayer() {
        return player;
    }
}
