package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public final class CoreTarkovcraftCommand {

    public static final String ROOT_NODE = "tarkovcraft";
    public static final SimpleCommandExceptionType INVALID_ENTITY = new SimpleCommandExceptionType(Component.translatable("command.tarkovcraft_core.exception.invalid_entity"));

    public static Predicate<CommandSourceStack> gameMasterOnly() {
        return src -> src.hasPermission(2);
    }

    public static void create(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
                Commands.literal(ROOT_NODE)
                        .then(
                                // tarkovcraft notification ...
                                NotificationSubCommand.node()
                                        .requires(gameMasterOnly())
                        )
                        .then(
                                // tarkovcraft attribute ...
                                AttributeSubCommand.node(context)
                                        .requires(gameMasterOnly())
                        )
                        .then(
                                // tarkovcraft skill ...
                                SkillSubCommand.node(context)
                                        .requires(gameMasterOnly())
                        )
                        .then(
                                // tarkovcraft stat ...
                                StatSubCommand.node(context)
                                        .requires(gameMasterOnly())
                        )
        );
    }


}
