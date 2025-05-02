package tnt.tarkovcraft.core.common.skill;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;
import tnt.tarkovcraft.core.network.message.S2C_SendDataAttachments;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;

import java.util.Collection;
import java.util.function.Supplier;

public final class SkillSystem {

    public static final Marker MARKER = MarkerManager.getMarker("SkillSystem");

    public static boolean isSkillSystemEnabled() {
        return TarkovCraftCore.getConfig().skillSystemConfig.skillSystemEnabled;
    }

    public static boolean isMemoryEnabled() {
        SkillSystemConfig cfg = TarkovCraftCore.getConfig().skillSystemConfig;
        return cfg.enableSkillExperienceLoss;
    }

    public static boolean isLevelMemoryEnabled() {
        SkillSystemConfig cfg = TarkovCraftCore.getConfig().skillSystemConfig;
        return cfg.enableSkillLevelLoss;
    }

    public static void synchronize(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new S2C_SendDataAttachments(serverPlayer, CoreDataAttachments.SKILL.get()));
        }
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity, float multiplier, Context context) {
        if (!isSkillSystemEnabled())
            return false;
        SkillData data = entity.getData(CoreDataAttachments.SKILL);
        return getTriggerables(entity.registryAccess(), event).stream()
                .filter(SkillDefinition::isEnabled)
                .anyMatch(definition -> data.trigger(event, definition, multiplier, entity, context));
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier, Context context) {
        return trigger(event.get(), entity, multiplier, context);
    }

    public static boolean trigger(Holder<SkillTriggerEvent> event, Entity entity, float multiplier, Context context) {
        return trigger(event.value(), entity, multiplier, context);
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity, float multiplier) {
        return trigger(event, entity, multiplier, ContextImpl.empty());
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier) {
        return trigger(event, entity, multiplier, ContextImpl.empty());
    }

    public static boolean trigger(Holder<SkillTriggerEvent> event, Entity entity, float multiplier) {
        return trigger(event, entity, multiplier, ContextImpl.empty());
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static boolean trigger(Holder<SkillTriggerEvent> event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static void triggerAndSynchronize(SkillTriggerEvent event, Entity entity, float multiplier, Context context) {
        if (trigger(event, entity, multiplier, context)) {
            synchronize(entity);
        }
    }

    public static void triggerAndSynchronize(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier, Context context) {
        triggerAndSynchronize(event.get(), entity, multiplier, context);
    }

    public static void triggerAndSynchronize(Holder<SkillTriggerEvent> event, Entity entity, float multiplier, Context context) {
        triggerAndSynchronize(event.value(), entity, multiplier, context);
    }

    public static void triggerAndSynchronize(SkillTriggerEvent event, Entity entity, float multiplier) {
        triggerAndSynchronize(event, entity, multiplier, ContextImpl.empty());
    }

    public static void triggerAndSynchronize(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier) {
        triggerAndSynchronize(event, entity, multiplier, ContextImpl.empty());
    }

    public static void triggerAndSynchronize(Holder<SkillTriggerEvent> event, Entity entity, float multiplier) {
        triggerAndSynchronize(event, entity, multiplier, ContextImpl.empty());
    }

    public static void triggerAndSynchronize(SkillTriggerEvent event, Entity entity) {
        triggerAndSynchronize(event, entity, 1.0F);
    }

    public static void triggerAndSynchronize(Supplier<SkillTriggerEvent> event, Entity entity) {
        triggerAndSynchronize(event, entity, 1.0F);
    }

    public static void triggerAndSynchronize(Holder<SkillTriggerEvent> event, Entity entity) {
        triggerAndSynchronize(event, entity, 1.0F);
    }

    public static Collection<SkillDefinition> getTriggerables(RegistryAccess access, SkillTriggerEvent event) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.SKILL_DEFINITION);
        return registry.listElements()
                .map(Holder.Reference::value)
                .filter(definition -> {
                    for (SkillTrackerDefinition trackerDefinition : definition.getTrackers()) {
                        if (trackerDefinition.event().equals(event)) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }
}
