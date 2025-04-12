package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFixedCodec;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerDefinition;

import java.util.Collection;
import java.util.List;

public class SkillDefinition {

    public static final Codec<SkillDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(t -> t.name),
            SkillTrackerDefinition.CODEC.listOf().fieldOf("trackers").forGetter(t -> t.trackers)
    ).apply(instance, SkillDefinition::new));
    public static final Codec<Holder<SkillDefinition>> CODEC = RegistryFixedCodec.create(TarkovCraftRegistries.DatapackKeys.SKILL_DEFINITION);

    private final Component name;
    private final List<SkillTrackerDefinition> trackers;

    public SkillDefinition(Component name, List<SkillTrackerDefinition> trackers) {
        this.name = name;
        this.trackers = trackers;
    }

    public Skill instance() {
        return null;
    }

    public Collection<SkillTrackerDefinition> getTrackers() {
        return this.trackers;
    }

    public Component getName() {
        return this.name;
    }
}
