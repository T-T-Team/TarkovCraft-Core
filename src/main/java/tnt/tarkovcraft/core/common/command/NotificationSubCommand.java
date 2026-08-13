package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;
import tnt.tarkovcraft.core.common.Notification;

import java.util.Collection;

public final class NotificationSubCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> node() {
        return Commands.literal("notification")
                .then(
                        Commands.argument("target", EntityArgument.players())
                                .then(
                                        Commands.argument("severity", EnumArgument.enumArgument(Notification.Severity.class))
                                                .then(
                                                        Commands.argument("content", StringArgumentType.string())
                                                                .executes(NotificationSubCommand::sendNotification)
                                                                .then(
                                                                        Commands.argument("lifetime", TimeArgument.time(20))
                                                                                .executes(NotificationSubCommand::sendNotificationWithLifetime)
                                                                )
                                                )
                                )
                );
    }

    private static int sendNotification(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
        Notification.Severity severity = ctx.getArgument("severity", Notification.Severity.class);
        String content = StringArgumentType.getString(ctx, "content");
        createNotificationAndSend(targets, severity, content, Notification.DEFAULT_LIFETIME);
        return 0;
    }

    private static int sendNotificationWithLifetime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
        Notification.Severity severity = ctx.getArgument("severity", Notification.Severity.class);
        String content = StringArgumentType.getString(ctx, "content");
        int lifetime = ctx.getArgument("lifetime", Integer.class);
        createNotificationAndSend(targets, severity, content, lifetime);
        return 0;
    }

    private static void createNotificationAndSend(Collection<ServerPlayer> players, Notification.Severity severity, String content, int lifetime) {
        Notification notification = Notification.of(severity, Component.literal(content), lifetime);
        players.forEach(notification::send);
    }
}
