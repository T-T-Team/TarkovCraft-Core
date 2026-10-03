package tnt.tarkovcraft.core.common.skill;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrigger;
import tnt.tarkovcraft.core.server.packs.resources.IdResource;
import tnt.tarkovcraft.core.server.packs.resources.SimpleJsonResourceStackReloadListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class SkillSystem extends SimpleJsonResourceStackReloadListener<SkillDefinition> {

    public static final Marker MARKER = MarkerManager.getMarker("SkillSystem");
    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("skill_system");
    private static final SkillSystem INSTANCE = new SkillSystem();
    private static final Multimap<SkillTrigger, Identifier> TRIGGER_CACHE = ArrayListMultimap.create();

    private final Map<Identifier, IdResource<SkillDefinition>> byId = new HashMap<>();

    private SkillSystem() {
        super(SkillDefinition.CODEC, FileToIdConverter.json("tarkovcraft/skill"));
    }

    public static void register(AddServerReloadListenersEvent event) {
        event.addListener(IDENTIFIER, INSTANCE);
    }

    public static Optional<IdResource<SkillDefinition>> getDefinition(Identifier identifier) {
        return Optional.ofNullable(INSTANCE.byId.get(identifier));
    }

    public static Collection<Identifier> listAvailableSkills() {
        return INSTANCE.byId.keySet();
    }

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

    public static boolean trigger(SkillTrigger event, Entity entity, float multiplier) {
        if (!isSkillSystemEnabled())
            return false;
        SkillData data = entity.getData(CoreDataAttachments.SKILL);
        var skills = TRIGGER_CACHE.get(event);
        boolean anyTrigger = false;
        for (var skill : skills) {
            if (data.trigger(event, skill, multiplier, entity)) {
                anyTrigger = true;
            }
        }
        return anyTrigger;
    }

    public static boolean trigger(Supplier<SkillTrigger> event, Entity entity, float multiplier) {
        return trigger(event.get(), entity, multiplier);
    }

    public static boolean trigger(Holder<SkillTrigger> event, Entity entity, float multiplier) {
        return trigger(event.value(), entity, multiplier);
    }

    public static boolean trigger(SkillTrigger event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static boolean trigger(Supplier<SkillTrigger> event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static boolean trigger(Holder<SkillTrigger> event, Entity entity) {
        return trigger(event, entity, 1.0F);
    }

    public static void triggerAndSynchronize(SkillTrigger event, Entity entity, float multiplier) {
        if (trigger(event, entity, multiplier)) {
            synchronize(entity);
        }
    }

    public static void triggerAndSynchronize(Supplier<SkillTrigger> event, Entity entity, float multiplier) {
        triggerAndSynchronize(event.get(), entity, multiplier);
    }

    public static void triggerAndSynchronize(Holder<SkillTrigger> event, Entity entity, float multiplier) {
        triggerAndSynchronize(event.value(), entity, multiplier);
    }

    public static void triggerAndSynchronize(SkillTrigger event, Entity entity) {
        triggerAndSynchronize(event, entity, 1.0F);
    }

    public static void triggerAndSynchronize(Supplier<SkillTrigger> event, Entity entity) {
        triggerAndSynchronize(event, entity, 1.0F);
    }

    public static void triggerAndSynchronize(Holder<SkillTrigger> event, Entity entity) {
        triggerAndSynchronize(event, entity, 1.0F);
    }

    @Override
    protected void apply(Map<Identifier, SkillDefinition> preparations, ResourceManager manager, ProfilerFiller profiler) {
        this.byId.clear();
        for (var entry : preparations.entrySet()) {
            Identifier id = entry.getKey();
            SkillDefinition definition = entry.getValue();
            if (!definition.enabled()) {
                TarkovCraftCore.LOGGER.debug(MARKER, "Skill {} is disabled, skipping loading", id);
                continue;
            }
            this.byId.put(id, new IdResource<>(id, definition));
        }
        reloadCache();
    }

    @Override
    protected void validateResultItem(Identifier id, SkillDefinition item) {
        SkillDefinition.validate(item);
    }

    @Override
    protected SkillDefinition mergeResources(Identifier id, SkillDefinition item, SkillDefinition overridingItem) {
        return SkillDefinition.merge(item, overridingItem);
    }

    /*public void synchronizeFromServer(Map<Identifier, SkillDefinition> definitionMap) {
        this.byId.clear();
        this.byId.putAll(definitionMap);
    }*/

    private static void reloadCache() {
        TRIGGER_CACHE.clear();
        for (var resource : INSTANCE.byId.values()) {
            for (var triggerHolder : resource.element().triggers()) {
                SkillTrigger trigger = triggerHolder.trigger();
                TRIGGER_CACHE.put(trigger, resource.identifier());
            }
        }
    }
}
