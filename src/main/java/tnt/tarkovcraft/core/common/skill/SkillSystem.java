package tnt.tarkovcraft.core.common.skill;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerEvent;

import java.util.Collection;

public final class SkillSystem {

    public static final Marker MARKER = MarkerManager.getMarker("SkillSystem");
    private static final Multimap<SkillTriggerEvent, SkillDefinition> TRIGGER_EVENT_CACHE = ArrayListMultimap.create();

    public static void trigger(SkillTriggerEvent event, Entity entity) {
        SkillData data = entity.getData(BaseDataAttachments.SKILL);
        if (TRIGGER_EVENT_CACHE.isEmpty())
            reloadCache(entity.registryAccess());
        Collection<SkillDefinition> triggerDefs = TRIGGER_EVENT_CACHE.get(event);
        for (SkillDefinition def : triggerDefs) {

        }
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
}
