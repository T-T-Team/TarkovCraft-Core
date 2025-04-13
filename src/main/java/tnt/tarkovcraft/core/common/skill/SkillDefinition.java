package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFixedCodec;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.trigger.condition.SkillTriggerCondition;

import java.util.*;

public class SkillDefinition {

    public static final Codec<SkillDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(t -> t.name),
            Codec.unboundedMap(
                    UUIDUtil.STRING_CODEC,
                    SkillTrackerDefinition.CODEC
            ).fieldOf("trackers").forGetter(t -> t.trackers)
    ).apply(instance, SkillDefinition::new));
    public static final Codec<Holder<SkillDefinition>> CODEC = RegistryFixedCodec.create(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION);

    private final Component name;
    private final Map<UUID, SkillTrackerDefinition> trackers;

    public SkillDefinition(Component name, Map<UUID, SkillTrackerDefinition> trackers) {
        this.name = name;
        this.trackers = trackers;
    }

    public Skill instance(RegistryAccess access) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION);
        Holder<SkillDefinition> reference = registry.wrapAsHolder(this);
        List<Skill.SkillTrackerData> trackerDataList = new ArrayList<>();
        for (Map.Entry<UUID, SkillTrackerDefinition> entry : this.trackers.entrySet()) {
            trackerDataList.add(new Skill.SkillTrackerData(entry.getKey(), entry.getValue().tracker()));
        }
        return new Skill(
                reference,
                trackerDataList
        );
    }

    public List<SkillTriggerCondition> getTrackerConditions(UUID id) {
        SkillTrackerDefinition definition = this.trackers.get(id);
        if (definition == null) {
            return Collections.emptyList();
        }
        return definition.conditions();
    }

    public Collection<SkillTrackerDefinition> getTrackers() {
        return this.trackers.values();
    }

    public Component getName() {
        return this.name;
    }
}
