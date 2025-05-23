package tnt.tarkovcraft.core.common.statistic;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKey;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.Map;
import java.util.function.LongBinaryOperator;

public final class StatisticTracker implements Synchronizable<StatisticTracker> {

    public static final Codec<StatisticTracker> CODEC = Codec.unboundedMap(CoreRegistries.STATISTICS.byNameCodec(), Codec.LONG)
            .xmap(StatisticTracker::new, tracker -> tracker.stats);
    public static final ContextKey<StatisticTracker> TRACKER = new ContextKey<>(TarkovCraftCore.createResourceLocation("stat_tracker"));
    private final Object2LongMap<Statistic> stats;

    public StatisticTracker() {
        this.stats = new Object2LongOpenHashMap<>();
    }

    private StatisticTracker(Map<Statistic, Long> stats) {
        this.stats = new Object2LongOpenHashMap<>(stats);
    }

    public static void increment(IAttachmentHolder holder, Holder<Statistic> stat) {
        increment(holder, stat.value());
    }

    public static void increment(IAttachmentHolder holder, Holder<Statistic> stat, long amount) {
        increment(holder, stat.value(), amount);
    }

    public static void increment(IAttachmentHolder holder, Statistic stat) {
        increment(holder, stat, 1L);
    }

    public static void increment(IAttachmentHolder holder, Statistic stat, long amount) {
        holder.getData(CoreDataAttachments.STATISTICS).increment(stat, amount);
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new S2C_SendDataAttachments(player, CoreDataAttachments.STATISTICS.get()));
        }
    }

    public static boolean incrementOptional(IAttachmentHolder holder, Holder<Statistic> stat) {
        return incrementOptional(holder, stat.value());
    }

    public static boolean incrementOptional(IAttachmentHolder holder, Holder<Statistic> stat, long amount) {
        return incrementOptional(holder, stat.value(), amount);
    }

    public static boolean incrementOptional(IAttachmentHolder holder, Statistic stat) {
        return incrementOptional(holder, stat, 1L);
    }

    public static boolean incrementOptional(IAttachmentHolder holder, Statistic statistic, long amount) {
        if (holder.hasData(CoreDataAttachments.STATISTICS)) {
            increment(holder, statistic, amount);
            return true;
        }
        return false;
    }

    public static void replace(IAttachmentHolder holder, Holder<Statistic> stat, long amount, LongBinaryOperator replacer) {
        replace(holder, stat.value(), amount, replacer);
    }

    public static void replace(IAttachmentHolder holder, Statistic statistic, long amount, LongBinaryOperator replacer) {
        StatisticTracker tracker = holder.getData(CoreDataAttachments.STATISTICS);
        long existing = tracker.get(statistic);
        long newValue = replacer.applyAsLong(existing, amount);
        tracker.set(statistic, newValue);
        if (holder instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new S2C_SendDataAttachments(player, CoreDataAttachments.STATISTICS.get()));
        }
    }

    public static boolean replaceOptional(IAttachmentHolder holder, Holder<Statistic> stat, long amount, LongBinaryOperator replacer) {
        return replaceOptional(holder, stat.value(), amount, replacer);
    }

    public static boolean replaceOptional(IAttachmentHolder holder, Statistic statistic, long amount, LongBinaryOperator replacer) {
        if (holder.hasData(CoreDataAttachments.STATISTICS)) {
            replace(holder, statistic, amount, replacer);
            return true;
        }
        return false;
    }

    public long get(Statistic stat) {
        return this.stats.getLong(stat);
    }

    public void increment(Statistic stat) {
        this.increment(stat, 1L);
    }

    public void increment(Statistic stat, long amount) {
        this.set(stat, this.get(stat) + amount);
    }

    public void set(Statistic stat, long amount) {
        this.stats.put(stat, Math.clamp(amount, 0L, Long.MAX_VALUE));
    }

    public void resetStatistics() {
        this.stats.clear();
    }

    @Override
    public Codec<StatisticTracker> networkCodec() {
        return CODEC;
    }
}
