package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionProviderCheck;

public final class CoreTarkovcraftCommand {

    public static final String ROOT_NODE = "tarkovcraft";
    public static final SimpleCommandExceptionType INVALID_ENTITY = new SimpleCommandExceptionType(Component.translatable("command.tarkovcraft_core.exception.invalid_entity"));

    public static PermissionProviderCheck<CommandSourceStack> gameMasterOnly() {
        return Commands.hasPermission(Commands.LEVEL_GAMEMASTERS);
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
