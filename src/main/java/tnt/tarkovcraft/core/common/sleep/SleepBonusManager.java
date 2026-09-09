package tnt.tarkovcraft.core.common.sleep;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SleepBonusManager extends SimpleJsonResourceReloadListener<SleepBonus> {

    public static final Marker MARKER = MarkerManager.getMarker("SleepBonusManager");
    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("sleep_bonus");

    private final List<SleepBonus> bonuses = new ArrayList<>();

    public SleepBonusManager() {
        super(SleepBonus.CODEC, FileToIdConverter.json("tarkovcraft/sleep_bonus"));
    }

    public void trigger(MinecraftServer server, long sleepDuration) {
        PlayerList playerList = server.getPlayerList();
        playerList.getPlayers().forEach(player -> {
            if (player.isSleeping() && player.isSleepingLongEnough()) {
                this.bonuses.forEach(bonus -> bonus.function().apply(player, sleepDuration));
            }
        });
    }

    @Override
    protected void apply(Map<Identifier, SleepBonus> preparations, ResourceManager manager, ProfilerFiller profiler) {
        this.bonuses.clear();
        this.bonuses.addAll(preparations.values());
        TarkovCraftCore.LOGGER.debug(MARKER, "Loaded {} sleep bonuses", this.bonuses.size());
    }
}
