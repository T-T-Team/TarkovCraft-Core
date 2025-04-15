package tnt.tarkovcraft.core.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.attribute.SprintAttributeListener;
import tnt.tarkovcraft.core.common.energy.MovementStamina;
import tnt.tarkovcraft.core.common.event.EntityAttributeEvent;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerEvents;
import tnt.tarkovcraft.core.common.skill.SkillSystem;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.Arrays;

public final class TarkovCraftCoreEventHandler {

    @SubscribeEvent
    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        TarkovCraftCommand.create(dispatcher, context);
    }

    @SubscribeEvent
    private void onPlayerLoggingIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide())
            return;
        // Reset states
        EntityAttributeData attributeData = player.getData(CoreDataAttachments.ENTITY_ATTRIBUTES);
        MovementStamina stamina = player.getData(CoreDataAttachments.STAMINA);
        stamina.setStamina(attributeData, Integer.MAX_VALUE);
        // Sync payload
        S2C_SendDataAttachments payload = new S2C_SendDataAttachments(player, Arrays.asList(
                CoreDataAttachments.MAIL_MANAGER.get(),
                CoreDataAttachments.ENTITY_ATTRIBUTES.get(),
                CoreDataAttachments.STAMINA.get(),
                CoreDataAttachments.SKILL.get()
        ));
        PacketDistributor.sendToPlayer((ServerPlayer) player, payload);
    }

    @SubscribeEvent
    private void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        player.getData(CoreDataAttachments.STAMINA).update(player);
        player.getData(CoreDataAttachments.ENTITY_ATTRIBUTES).update();
        SkillSystem.trigger(CoreSkillTriggerEvents.PLAYER_TICK, player);
    }

    @SubscribeEvent
    private void onAttributeConstructing(EntityAttributeEvent.AttributeInstanceConstructing event) {
        if (CoreAttributes.SPRINT.is(event.getAttribute().identifier())) {
            AttributeInstance instance = event.getInstance();
            instance.addListener(new SprintAttributeListener(instance::booleanValue, event::getEntity));
        }
    }
}
