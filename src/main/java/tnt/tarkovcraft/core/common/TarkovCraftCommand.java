package tnt.tarkovcraft.core.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.MailSystem;

import java.util.Collection;

public final class TarkovCraftCommand {

    public static void create(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("tarkovcraft")
                        .then(
                                Commands.literal("message")
                                        .requires(MailSystem::isCommandAllowed)
                                        .then(
                                                Commands.argument("target", EntityArgument.player())
                                                        .then(
                                                                Commands.argument("content", StringArgumentType.string())
                                                                        .executes(TarkovCraftCommand::sendMessage)
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("notification")
                                        .requires(src -> src.hasPermission(2))
                                        .then(
                                                Commands.argument("target", EntityArgument.players())
                                                        .then(
                                                                Commands.argument("severity", EnumArgument.enumArgument(IValidationResult.Severity.class))
                                                                        .then(
                                                                                Commands.argument("content", StringArgumentType.string())
                                                                                        .executes(TarkovCraftCommand::sendNotification)
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    private static int sendNotification(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
        IValidationResult.Severity severity = ctx.getArgument("severity", IValidationResult.Severity.class);
        String content = StringArgumentType.getString(ctx, "content");
        Notification notification = Notification.of(severity, Component.literal(content));
        for (ServerPlayer player : targets) {
            notification.send(player);
        }
        return 0;
    }

    private static int sendMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack stack = context.getSource();
        ServerPlayer sender = stack.getPlayer();
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        MailSource source = sender != null ? MailSource.player(sender) : MailSource.SYSTEM;
        String message = StringArgumentType.getString(context, "content");
        MailSystem.sendMessage(target, source, MailMessage.simpleChatMessage(source, message));
        return 0;
    }
}
