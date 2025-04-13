package tnt.tarkovcraft.core.common.skill;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerEvent;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextImpl;

import java.util.Collection;
import java.util.function.Supplier;

public final class SkillSystem {

    public static final Marker MARKER = MarkerManager.getMarker("SkillSystem");
    private static final Multimap<SkillTriggerEvent, SkillDefinition> TRIGGER_EVENT_CACHE = ArrayListMultimap.create();

    public static boolean isSkillSystemEnabled() {
        return TarkovCraftCore.getConfig().skillSystemConfig.skillSystemEnabled;
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity, float multiplier, Context context) {
        if (entity.level().isClientSide())
            return false;
        if (!isSkillSystemEnabled())
            return false;
        SkillData data = entity.getData(BaseDataAttachments.SKILL);
        return getTriggerables(entity.registryAccess(), event).stream()
                .anyMatch(definition -> data.trigger(event, definition, multiplier, context));
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier, Context context) {
        return trigger(event.get(), entity, multiplier, context);
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity, float multiplier) {
        return trigger(event, entity, multiplier, ContextImpl.empty());
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity, float multiplier) {
        return trigger(event, entity, multiplier, ContextImpl.empty());
    }

    public static boolean trigger(SkillTriggerEvent event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static boolean trigger(Supplier<SkillTriggerEvent> event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static void reloadCache(HolderLookup.Provider provider) {
        TarkovCraftCore.LOGGER.debug(MARKER, "Reloading skill system cache");
        TRIGGER_EVENT_CACHE.clear();
        provider.lookup(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION).ifPresent(registryLookup -> {
            registryLookup.listElements().map(Holder.Reference::value).forEach(value -> {
                Collection<SkillTrackerDefinition> trackers = value.getTrackers();
                for (SkillTrackerDefinition tracker : trackers) {
                    SkillTriggerEvent event = tracker.event();
                    TRIGGER_EVENT_CACHE.put(event, value);
                }
            });
            TarkovCraftCore.LOGGER.debug(MARKER, "Reloaded skill system cache, contains {} events and total {} items", TRIGGER_EVENT_CACHE.keySet().size(), TRIGGER_EVENT_CACHE.size());
        });
    }

    public static Collection<SkillDefinition> getTriggerables(RegistryAccess access, SkillTriggerEvent event) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION);
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
        //if (TRIGGER_EVENT_CACHE.isEmpty())
        //    reloadCache(access);
        //return TRIGGER_EVENT_CACHE.get(event);
    }
}
