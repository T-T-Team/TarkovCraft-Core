package tnt.tarkovcraft.core.common.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.attribute.modifier.SetValueAttributeModifier;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import javax.annotation.Nullable;
import java.util.Collection;

public final class AttributeSubCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> node(CommandBuildContext context) {
        return Commands.literal("attribute")
                .requires(CoreTarkovcraftCommand.gameMasterOnly())
                .then(
                        Commands.argument("attributeId", ResourceArgument.resource(context, CoreRegistries.Keys.ATTRIBUTE))
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
                                                                        Commands.argument("modifierId", ResourceLocationArgument.id())
                                                                                .then(
                                                                                        Commands.argument("setValue", DoubleArgumentType.doubleArg())
                                                                                                .executes(ctx -> updateAttributeModifier(ctx, false))
                                                                                )
                                                                )
                                                )
                                                .then(
                                                        Commands.literal("removeModifier")
                                                                .then(
                                                                        Commands.argument("modifierId", ResourceLocationArgument.id())
                                                                                .executes(ctx -> updateAttributeModifier(ctx, true))
                                                                )
                                                )
                                )
                );
    }

    private static int updateAttributeModifier(CommandContext<CommandSourceStack> ctx, boolean remove) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "target");
        Holder.Reference<Attribute> reference = ResourceArgument.getResource(ctx, "attributeId", CoreRegistries.Keys.ATTRIBUTE);
        Attribute attribute = reference.value();
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "modifierId");
        EntityAttributeData attributeData = entity.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        AttributeInstance instance = attributeData.getAttribute(attribute);
        instance.removeModifier(id);
        if (!remove) {
            double value = DoubleArgumentType.getDouble(ctx, "setValue");
            AttributeModifier modifier = new SetValueAttributeModifier(id, value, Integer.MAX_VALUE);
            instance.addModifier(modifier);
        }
        AttributeSystem.sync(entity);
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
            throw CoreTarkovcraftCommand.INVALID_ENTITY.create();
        }
        Holder.Reference<Attribute> reference = ResourceArgument.getResource(ctx, "attributeId", CoreRegistries.Keys.ATTRIBUTE);
        Attribute attribute = reference.value();
        if (!forceInit && !attachmentHolder.hasData(CoreDataAttachments.ENTITY_ATTRIBUTES)) {
            return printAttributeInfo(entity.getDisplayName(), ctx.getSource(), attribute.createInstance((Entity) attachmentHolder));
        }
        EntityAttributeData entityAttributeData = attachmentHolder.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        if (!forceInit && !entityAttributeData.hasAttribute(attribute)) { // avoid unnecessary creation of attribute instance in entity data
            return printAttributeInfo(entity.getDisplayName(), ctx.getSource(), attribute.createInstance((Entity) attachmentHolder));
        }
        return printAttributeInfo(entity.getDisplayName(), ctx.getSource(), entityAttributeData.getAttribute(attribute));
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
