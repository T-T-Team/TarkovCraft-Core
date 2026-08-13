package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.statistic.Statistic;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;

public final class StatSubCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> node(CommandBuildContext context) {
        return Commands.literal("stat")
                .then(
                        Commands.argument("statId", ResourceArgument.resource(context, CoreRegistries.Keys.STATISTICS))
                                .then(
                                        Commands.argument("target", EntityArgument.player())
                                                .then(
                                                        Commands.argument("value", LongArgumentType.longArg(0))
                                                                .executes(StatSubCommand::setStatValue)
                                                )
                                                .then(
                                                        Commands.literal("clear")
                                                                .executes(StatSubCommand::clearStat)
                                                )
                                )
                )
                .then(
                        Commands.literal("clearAll")
                                .executes(StatSubCommand::resetAllStats)
                );
    }

    private static int setStatValue(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        Statistic statistic = ResourceArgument.getResource(ctx, "statId", CoreRegistries.Keys.STATISTICS).value();
        long value = LongArgumentType.getLong(ctx, "value");
        StatisticTracker tracker = target.getData(CoreDataAttachments.STATISTICS);
        tracker.set(statistic, value);
        target.syncData(CoreDataAttachments.STATISTICS);
        return 0;
    }

    private static int clearStat(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        Statistic statistic = ResourceArgument.getResource(ctx, "statId", CoreRegistries.Keys.STATISTICS).value();
        StatisticTracker tracker = target.getData(CoreDataAttachments.STATISTICS);
        tracker.resetCounter(statistic);
        target.syncData(CoreDataAttachments.STATISTICS);
        return 0;
    }

    private static int resetAllStats(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(ctx, "target");
        StatisticTracker tracker = target.getData(CoreDataAttachments.STATISTICS);
        tracker.resetAllCounters();
        target.syncData(CoreDataAttachments.STATISTICS);
        return 0;
    }
}
