package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SkillDefinition {

    public static final Codec<SkillDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(t -> t.enabled),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(t -> t.name),
            SkillLevelDefinition.CODEC.optionalFieldOf("leveling", SkillLevelDefinition.DEFAULT).forGetter(t -> t.levelDefinition),
            SkillMemoryConfiguration.CODEC.optionalFieldOf("memory", SkillMemoryConfiguration.NO_LOSS).forGetter(t -> t.memory),
            CoreRegistries.ATTRIBUTE.holderByNameCodec().listOf().optionalFieldOf("groupLevelModifiers", Collections.emptyList()).forGetter(t -> t.groupLevelingModifiers),
            SkillTrackerDefinition.CODEC.listOf().fieldOf("trackers").forGetter(t -> t.trackers),
            SkillStatDefinition.CODEC.listOf().fieldOf("stats").forGetter(t -> t.stats)
    ).apply(instance, SkillDefinition::new));
    public static final Codec<Holder<SkillDefinition>> CODEC = RegistryFixedCodec.create(CoreRegistries.DatapackKeys.SKILL_DEFINITION);

    private final boolean enabled;
    private final Component name;
    private final SkillLevelDefinition levelDefinition;
    private final SkillMemoryConfiguration memory;
    private final List<Holder<Attribute>> groupLevelingModifiers;
    private final List<SkillTrackerDefinition> trackers;
    private final List<SkillStatDefinition> stats;

    public SkillDefinition(boolean enabled, Component name, SkillLevelDefinition levelDefinition, SkillMemoryConfiguration memory, List<Holder<Attribute>> groupLevelingModifiers, List<SkillTrackerDefinition> trackers, List<SkillStatDefinition> stats) {
        this.enabled = enabled;
        this.name = name;
        this.levelDefinition = levelDefinition;
        this.memory = memory;
        this.groupLevelingModifiers = groupLevelingModifiers;
        this.trackers = trackers;
        this.stats = stats;
    }

    public static ResourceLocation getIcon(Holder<SkillDefinition> holder) {
        ResourceLocation skillIdentifier = holder.getKey().location();
        return skillIdentifier.withPath(pth -> "textures/icons/skill/" + pth + ".png");
    }

    public Skill instance(RegistryAccess access) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.SKILL_DEFINITION);
        Holder<SkillDefinition> reference = registry.wrapAsHolder(this);
        return new Skill(reference);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SkillLevelDefinition getLevelDefinition() {
        return this.levelDefinition;
    }

    public SkillMemoryConfiguration getMemory() {
        return memory;
    }

    public List<Holder<Attribute>> getGroupLevelingModifiers() {
        return groupLevelingModifiers;
    }

    public Collection<SkillTrackerDefinition> getTrackers() {
        return this.trackers;
    }

    public List<SkillStatDefinition> getStats() {
        return stats;
    }

    public Component getName() {
        return this.name;
    }
}
