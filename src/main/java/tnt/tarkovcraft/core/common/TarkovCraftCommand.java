package tnt.tarkovcraft.core.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.EnumArgument;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.attribute.modifier.SetValueAttributeModifier;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.mail.MailMessage;
import tnt.tarkovcraft.core.common.mail.MailSource;
import tnt.tarkovcraft.core.common.mail.MailSystem;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public final class TarkovCraftCommand {

    private static final SimpleCommandExceptionType INVALID_ENTITY = new SimpleCommandExceptionType(Component.translatable("command.tarkovcraft_core.exception.invalid_entity"));

    public static void create(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
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
                                                                                        .then(
                                                                                                Commands.argument("lifetime", TimeArgument.time(20))
                                                                                                        .executes(TarkovCraftCommand::sendNotificationWithLifetime)
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("attribute")
                                        .requires(src -> src.hasPermission(2))
                                        .then(
                                                Commands.argument("attributeId", ResourceArgument.resource(context, TarkovCraftRegistries.Keys.ATTRIBUTE))
                                                        .executes(ctx -> getAttributeInfo(ctx, null, false))
                                                        .then(
                                                                Commands.argument("target", EntityArgument.entity())
                                                                        .executes(ctx -> getAttributeInfo(ctx, EntityArgument.getEntity(ctx, "target"), false))
                                                                        .then(
                                                                                Commands.literal("force")
                                                                                        .executes(ctx -> getAttributeInfo(ctx, EntityArgument.getEntity(ctx, "target"), true))
                                                                        )
                                                                        .then(
                                                                                Commands.literal("addModifier")
                                                                                        .then(
                                                                                                Commands.argument("setValue", DoubleArgumentType.doubleArg())
                                                                                                        .executes(ctx -> updateAttributeModifier(ctx, false))
                                                                                        )
                                                                        )
                                                                        .then(
                                                                                Commands.literal("removeModifier")
                                                                                        .executes(ctx -> updateAttributeModifier(ctx, true))
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    private static int updateAttributeModifier(CommandContext<CommandSourceStack> ctx, boolean remove) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "target");
        Holder.Reference<Attribute> reference = ResourceArgument.getResource(ctx, "attributeId", TarkovCraftRegistries.Keys.ATTRIBUTE);
        Attribute attribute = reference.value();
        UUID systemModifierId = Util.NIL_UUID;
        EntityAttributeData attributeData = entity.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        AttributeInstance instance = attributeData.getAttribute(attribute);
        instance.removeModifier(systemModifierId);
        if (!remove) {
            double value = DoubleArgumentType.getDouble(ctx, "setValue");
            AttributeModifier modifier = new SetValueAttributeModifier(systemModifierId, value, -1);
            instance.addModifier(modifier);
        }
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new S2C_SendDataAttachments(serverPlayer, BaseDataAttachments.ENTITY_ATTRIBUTES.get()));
        }
        return 0;
    }

    private static int getAttributeInfo(CommandContext<CommandSourceStack> ctx, @Nullable Entity entity, boolean forceInit) throws CommandSyntaxException {
        if (entity == null) {
            entity = ctx.getSource().getEntity();
        }
        IAttachmentHolder attachmentHolder = null;
        if (entity instanceof IAttachmentHolder holder) {
            attachmentHolder = holder;
        }
        if (attachmentHolder == null) {
            throw INVALID_ENTITY.create();
        }
        Holder.Reference<Attribute> reference = ResourceArgument.getResource(ctx, "attributeId", TarkovCraftRegistries.Keys.ATTRIBUTE);
        Attribute attribute = reference.value();
        if (!forceInit && !attachmentHolder.hasData(BaseDataAttachments.ENTITY_ATTRIBUTES)) {
            return printAttributeInfo(entity.getDisplayName(), ctx.getSource(), attribute.createInstance((Entity) attachmentHolder));
        }
        EntityAttributeData entityAttributeData = attachmentHolder.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        if (!forceInit && !entityAttributeData.hasAttribute(attribute)) { // avoid unnecessary creation of attribute instance in entity data
            return printAttributeInfo(entity.getDisplayName(), ctx.getSource(), attribute.createInstance((Entity) attachmentHolder));
        }
        return printAttributeInfo(entity.getDisplayName(), ctx.getSource(), entityAttributeData.getAttribute(attribute));
    }

    private static int sendNotification(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
        IValidationResult.Severity severity = ctx.getArgument("severity", IValidationResult.Severity.class);
        String content = StringArgumentType.getString(ctx, "content");
        createNotificationAndSend(targets, severity, content, Notification.DEFAULT_LIFETIME);
        return 0;
    }

    private static int sendNotificationWithLifetime(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "target");
        IValidationResult.Severity severity = ctx.getArgument("severity", IValidationResult.Severity.class);
        String content = StringArgumentType.getString(ctx, "content");
        int lifetime = ctx.getArgument("lifetime", Integer.class);
        createNotificationAndSend(targets, severity, content, lifetime);
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

    private static void createNotificationAndSend(Collection<ServerPlayer> players, IValidationResult.Severity severity, String content, int lifetime) {
        Notification notification = Notification.of(severity, Component.literal(content), lifetime);
        players.forEach(notification::send);
    }

    private static int printAttributeInfo(Component owner, CommandSourceStack stack, AttributeInstance instance) {
        Component separator = Component.literal("========================================").withStyle(ChatFormatting.GREEN);
        Attribute attribute = instance.getAttribute();
        stack.sendSystemMessage(separator);
        stack.sendSystemMessage(Component.literal("Owner: ").withStyle(ChatFormatting.AQUA).append(owner.copy().withStyle(ChatFormatting.YELLOW)));
        stack.sendSystemMessage(Component.literal("Attribute: ").withStyle(ChatFormatting.AQUA).append(attribute.getDisplayName().copy().withStyle(ChatFormatting.YELLOW)));
        stack.sendSystemMessage(Component.literal("Initial value: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(attribute.getBaseValue())).withStyle(ChatFormatting.YELLOW)));
        stack.sendSystemMessage(Component.literal("Value: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(instance.value())).withStyle(ChatFormatting.YELLOW)));
        stack.sendSystemMessage(Component.literal("Active Listeners: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(instance.getActiveListenerCount())).withStyle(ChatFormatting.YELLOW)));
        Collection<AttributeModifier> modifiers = instance.listModifiers().values();
        stack.sendSystemMessage(Component.literal("Modifiers: ").withStyle(ChatFormatting.AQUA).append(Component.literal(String.valueOf(modifiers.size())).withStyle(ChatFormatting.YELLOW)));
        for (AttributeModifier modifier : modifiers) {
            stack.sendSystemMessage(Component.literal(" - ").withStyle(ChatFormatting.GRAY).append(Component.literal(modifier.toString()).withStyle(ChatFormatting.YELLOW)));
        }
        stack.sendSystemMessage(separator);
        return 0;
    }
}
