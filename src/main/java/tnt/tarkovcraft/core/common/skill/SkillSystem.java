package tnt.tarkovcraft.core.common.skill;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;

import java.util.Collection;
import java.util.function.Supplier;

public final class SkillSystem {

    public static final Marker MARKER = MarkerManager.getMarker("SkillSystem");
    private static final Multimap<SkillTriggerEvent, SkillDefinition> TRIGGER_CACHE = ArrayListMultimap.create();

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
        entity.syncData(CoreDataAttachments.SKILL);
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity, float multiplier) {
        if (!isSkillSystemEnabled())
            return false;
        SkillData data = entity.getData(CoreDataAttachments.SKILL);
        Collection<SkillDefinition> definitions = TRIGGER_CACHE.get(event);
        boolean anyTrigger = false;
        for (SkillDefinition definition : definitions) {
            if (data.trigger(event, definition, multiplier, entity)) {
                anyTrigger = true;
            }
        }
        return anyTrigger;
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier) {
        return trigger(event.get(), entity, multiplier);
    }

    public static boolean trigger(Holder<SkillTriggerEvent> event, Entity entity, float multiplier) {
        return trigger(event.value(), entity, multiplier);
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

    public static void triggerAndSynchronize(SkillTriggerEvent event, Entity entity, float multiplier) {
        if (trigger(event, entity, multiplier)) {
            synchronize(entity);
        }
    }

    public static void triggerAndSynchronize(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier) {
        triggerAndSynchronize(event.get(), entity, multiplier);
    }

    public static void triggerAndSynchronize(Holder<SkillTriggerEvent> event, Entity entity, float multiplier) {
        triggerAndSynchronize(event.value(), entity, multiplier);
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

    @ApiStatus.Internal
    public static void onServerStarted(ServerStartedEvent event) {
        TRIGGER_CACHE.clear();
        MinecraftServer server = event.getServer();
        RegistryAccess access = server.registryAccess();
        HolderLookup.RegistryLookup<SkillDefinition> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.SKILL_DEFINITION);
        registry.listElements().map(Holder.Reference::value)
                .filter(SkillDefinition::enabled)
                .forEach(definition -> {
                    for (SkillTrackerDefinition trackerDefinition : definition.trackers()) {
                        SkillTriggerEvent triggerEvent = trackerDefinition.event();
                        TRIGGER_CACHE.put(triggerEvent, definition);
                    }
                });
    }
}
