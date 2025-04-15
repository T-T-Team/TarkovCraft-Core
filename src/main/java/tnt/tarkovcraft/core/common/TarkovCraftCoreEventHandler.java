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
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.common.energy.EnergyType;
import tnt.tarkovcraft.core.common.energy.MovementStamina;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
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
        EntityAttributeData attributeData = player.getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
        MovementStamina stamina = player.getData(BaseDataAttachments.STAMINA);
        stamina.set(attributeData, Integer.MAX_VALUE);
        // Sync payload
        S2C_SendDataAttachments payload = new S2C_SendDataAttachments(player, Arrays.asList(
                BaseDataAttachments.MAIL_MANAGER.get(),
                BaseDataAttachments.ENTITY_ATTRIBUTES.get(),
                BaseDataAttachments.STAMINA.get(),
                BaseDataAttachments.SKILL.get()
        ));
        PacketDistributor.sendToPlayer((ServerPlayer) player, payload);
    }

    @SubscribeEvent
    private void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        player.getData(BaseDataAttachments.STAMINA).update(player);
        player.getData(BaseDataAttachments.ENTITY_ATTRIBUTES).update();
        SkillSystem.trigger(CoreSkillTriggerEvents.PLAYER_TICK, player);
    }
}
