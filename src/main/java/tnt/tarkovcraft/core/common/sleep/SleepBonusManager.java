package tnt.tarkovcraft.core.common.sleep;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
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

public final class SleepBonusManager extends SimpleJsonResourceReloadListener {

    public static final Marker MARKER = MarkerManager.getMarker("SleepBonusManager");
    public static final ResourceLocation IDENTIFIER = TarkovCraftCore.createIdentifier("sleep_bonus");

    private final List<SleepBonus> bonuses = new ArrayList<>();

    public SleepBonusManager() {
        super(new Gson(), "tarkovcraft/sleep_bonus");
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
    protected void apply(Map<ResourceLocation, JsonElement> preparations, ResourceManager manager, ProfilerFiller profiler) {
        this.bonuses.clear();
        for (var entry : preparations.entrySet()) {
            try {
                DataResult<SleepBonus> result = SleepBonus.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
                SleepBonus bonus = result.getOrThrow();
                this.bonuses.add(bonus);
            } catch (Exception e) {
                TarkovCraftCore.LOGGER.error(MARKER, "Failed to load sleep bonus {}", entry.getKey(), e);
            }
        }
        TarkovCraftCore.LOGGER.debug(MARKER, "Loaded {} sleep bonuses", this.bonuses.size());
    }
}
