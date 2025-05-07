package tnt.tarkovcraft.core.common;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreSkillTriggerEvents;
import tnt.tarkovcraft.core.common.init.CoreStatistics;
import tnt.tarkovcraft.core.common.skill.SkillSystem;
import tnt.tarkovcraft.core.common.statistic.CustomStatTrackerProvider;
import tnt.tarkovcraft.core.common.statistic.Statistic;
import tnt.tarkovcraft.core.common.statistic.StatisticTracker;
import tnt.tarkovcraft.core.network.Synchronizable;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;

import java.util.ArrayList;
import java.util.List;

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
        MovementStaminaComponent stamina = EnergySystem.MOVEMENT_STAMINA.getComponent();
        stamina.setStamina(player, Integer.MAX_VALUE);
        // Sync payload
        PacketDistributor.sendToPlayer((ServerPlayer) player, this.getSyncPacket(player));
    }

    @SubscribeEvent
    private void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            S2C_SendDataAttachments packet = this.getSyncPacket(player);
            PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
        }
    }

    @SubscribeEvent
    private void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            S2C_SendDataAttachments packet = this.getSyncPacket(player);
            PacketDistributor.sendToPlayer((ServerPlayer) player, packet);
        }
    }

    @SubscribeEvent
    private void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        EnergySystem.MOVEMENT_STAMINA.getComponent().update(player);
        player.getData(CoreDataAttachments.ENTITY_ATTRIBUTES).update();
        SkillSystem.trigger(CoreSkillTriggerEvents.PLAYER_TICK, player);
    }

    @SubscribeEvent
    private void onEntityHurt(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        if (sourceEntity != null) {
            long distance = (long) (entity.distanceTo(sourceEntity) * 100);
            StatisticTracker.replace(sourceEntity, CoreStatistics.LONGEST_HIT, distance, Math::max);
        }
    }

    @SubscribeEvent
    private void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Entity killer = source.getEntity();
        if (entity == killer) {
            return; // do not count suicides
        }
        if (killer != null && killer.hasData(CoreDataAttachments.STATISTICS)) {
            StatisticTracker.increment(killer, CoreStatistics.KILLS);
            long distance = (long) (entity.distanceTo(killer) * 100);
            StatisticTracker.replace(killer, CoreStatistics.LONGEST_KILL, distance, Math::max);
            if (entity instanceof CustomStatTrackerProvider tracker) {
                Holder<Statistic> counter = tracker.getKillCounter(killer);
                if (counter != null) {
                    StatisticTracker.increment(killer, counter);
                }
            }
        }
        if (entity.hasData(CoreDataAttachments.STATISTICS)) {
            StatisticTracker.increment(entity, CoreStatistics.DEATHS);
            if (killer instanceof CustomStatTrackerProvider tracker) {
                Holder<Statistic> counter = tracker.getDeathCounter(entity);
                if (counter != null) {
                    StatisticTracker.increment(entity, counter);
                }
            }
        }
    }

    @SubscribeEvent
    private void onExperiencePickup(PlayerXpEvent.PickupXp event) {
        if (event.isCanceled())
            return;
        Player player = event.getEntity();
        ExperienceOrb orb = event.getOrb();
        int value = orb.getValue();
        SkillSystem.triggerAndSynchronize(CoreSkillTriggerEvents.XP_PICKUP, player, value);
    }

    private S2C_SendDataAttachments getSyncPacket(Player player) {
        List<AttachmentType<? extends Synchronizable<?>>> list = new ArrayList<>();
        list.add(CoreDataAttachments.MAIL_MANAGER.get());
        list.add(CoreDataAttachments.ENTITY_ATTRIBUTES.get());
        list.add(CoreDataAttachments.STATISTICS.get());
        list.add(CoreDataAttachments.SKILL.get());
        if (EnergySystem.MOVEMENT_STAMINA.isVanilla()) {
            list.add(CoreDataAttachments.MOVEMENT_STAMINA.get());
        }
        return new S2C_SendDataAttachments(player, list);
    }
}
