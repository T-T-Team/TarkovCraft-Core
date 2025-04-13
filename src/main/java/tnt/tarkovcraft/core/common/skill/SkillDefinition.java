package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFixedCodec;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;

import java.util.Collection;
import java.util.List;

public class SkillDefinition {

    public static final Codec<SkillDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(t -> t.enabled),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(t -> t.name),
            SkillLevelDefinition.CODEC.optionalFieldOf("leveling", SkillLevelDefinition.DEFAULT).forGetter(t -> t.levelDefinition),
            SkillTrackerDefinition.CODEC.listOf().fieldOf("trackers").forGetter(t -> t.trackers)
    ).apply(instance, SkillDefinition::new));
    public static final Codec<Holder<SkillDefinition>> CODEC = RegistryFixedCodec.create(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION);

    private final boolean enabled;
    private final Component name;
    private final SkillLevelDefinition levelDefinition;
    private final List<SkillTrackerDefinition> trackers;

    public SkillDefinition(boolean enabled, Component name, SkillLevelDefinition levelDefinition, List<SkillTrackerDefinition> trackers) {
        this.enabled = enabled;
        this.name = name;
        this.levelDefinition = levelDefinition;
        this.trackers = trackers;
    }

    public Skill instance(RegistryAccess access) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION);
        Holder<SkillDefinition> reference = registry.wrapAsHolder(this);
        return new Skill(reference);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public SkillLevelDefinition getLevelDefinition() {
        return this.levelDefinition;
    }

    public Collection<SkillTrackerDefinition> getTrackers() {
        return this.trackers;
    }

    public Component getName() {
        return this.name;
    }
}
