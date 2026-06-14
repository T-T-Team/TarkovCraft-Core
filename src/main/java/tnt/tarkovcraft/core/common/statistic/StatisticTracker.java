package tnt.tarkovcraft.core.common.statistic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import tnt.tarkovcraft.core.api.AttachmentSyncCallbackListener;
import tnt.tarkovcraft.core.api.client.SynchronizableScreen;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.util.OwnerAttachmentSyncHandler;

import java.util.Map;
import java.util.function.LongBinaryOperator;

public final class StatisticTracker implements StatisticReader {

    public static final MapCodec<StatisticTracker> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(CoreRegistries.STATISTICS.byNameCodec(), Codec.LONG).fieldOf("statMap").forGetter(t -> t.stats)
    ).apply(instance, StatisticTracker::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, StatisticTracker> STREAM_CODEC = ByteBufCodecs.map(
            Object2LongOpenHashMap::new, ByteBufCodecs.registry(CoreRegistries.Keys.STATISTICS), ByteBufCodecs.LONG
    ).map(StatisticTracker::new, tracker -> (Object2LongOpenHashMap<Statistic>) tracker.stats);
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
        holder.syncData(CoreDataAttachments.STATISTICS);
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
        holder.syncData(CoreDataAttachments.STATISTICS);
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

    @Override
    public long get(Holder<Statistic> statistic) {
        return this.stats.getLong(statistic.value());
    }

    @Override
    public long getMinimum(Holder<Statistic> statistic, long minimum) {
        return Math.max(this.get(statistic), minimum);
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

    public static final class SyncHandler extends OwnerAttachmentSyncHandler<StatisticTracker> implements AttachmentSyncCallbackListener<StatisticTracker> {

        public SyncHandler() {
            super(StatisticTracker.STREAM_CODEC);
        }

        @Override
        public void onDataSynced(IAttachmentHolder holder, AttachmentType<StatisticTracker> attachmentType, StatisticTracker attachment) {
            TarkovCraftCoreClient.synchronizeCurrentScreen(SynchronizableScreen.STATISTICS);
        }
    }
}
