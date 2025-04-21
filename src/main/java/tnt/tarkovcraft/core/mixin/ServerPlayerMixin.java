package tnt.tarkovcraft.core.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import tnt.tarkovcraft.core.common.init.CoreStatistics;
import tnt.tarkovcraft.core.common.statistic.CustomStatTrackerProvider;
import tnt.tarkovcraft.core.common.statistic.Statistic;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntity implements CustomStatTrackerProvider {

    public ServerPlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public Holder<Statistic> getKillCounter(Entity killer) {
        // we killed a player
        return killer.getType() == EntityType.PLAYER ? CoreStatistics.PLAYER_KILLS : null;
    }

    @Override
    public Holder<Statistic> getDeathCounter(Entity victim) {
        // we were killed by a player
        return victim.getType() == EntityType.PLAYER ? CoreStatistics.PLAYER_DEATHS : null;
    }
}
