package tnt.tarkovcraft.core.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.MailSystem;

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
        );
    }

    public static int sendMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack stack = context.getSource();
        ServerPlayer sender = stack.getPlayer();
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        MailSource source = sender != null ? MailSource.player(sender) : MailSource.SYSTEM;
        String message = StringArgumentType.getString(context, "content");
        MailSystem.sendMessage(target, source, MailMessage.simpleChatMessage(source, message));
        return 0;
    }
}
