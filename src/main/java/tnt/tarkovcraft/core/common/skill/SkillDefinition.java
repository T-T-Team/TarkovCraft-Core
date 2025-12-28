package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryFixedCodec;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;

import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public record SkillDefinition(boolean enabled, Component name, SkillLevelDefinition levelDefinition, SkillMemoryConfiguration memory, List<Holder<Attribute>> groupLevelingModifiers, List<SkillTrackerDefinition> trackers, List<SkillStatDefinition> stats) {

    public static final Codec<SkillDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", true).forGetter(t -> t.enabled),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(t -> t.name),
            SkillLevelDefinition.CODEC.optionalFieldOf("leveling", SkillLevelDefinition.DEFAULT).forGetter(t -> t.levelDefinition),
            SkillMemoryConfiguration.CODEC.optionalFieldOf("memory", SkillMemoryConfiguration.NO_LOSS).forGetter(t -> t.memory),
            CoreRegistries.ATTRIBUTE.holderByNameCodec().listOf().optionalFieldOf("group_level_modifiers", Collections.emptyList()).forGetter(t -> t.groupLevelingModifiers),
            SkillTrackerDefinition.CODEC.listOf().fieldOf("trackers").forGetter(t -> t.trackers),
            SkillStatDefinition.CODEC.listOf().fieldOf("stats").forGetter(t -> t.stats)
    ).apply(instance, SkillDefinition::new));
    public static final Codec<Holder<SkillDefinition>> CODEC = RegistryFixedCodec.create(CoreRegistries.DatapackKeys.SKILL_DEFINITION);

    public static Identifier getIcon(Holder<SkillDefinition> holder) {
        Identifier skillIdentifier = holder.getKey().identifier();
        return skillIdentifier.withPath(pth -> "textures/icons/skill/" + pth + ".png");
    }

    public Skill instance(RegistryAccess access) {
        Registry<SkillDefinition> registry = access.lookupOrThrow(CoreRegistries.DatapackKeys.SKILL_DEFINITION);
        Holder<SkillDefinition> reference = registry.wrapAsHolder(this);
        return new Skill(reference);
    }

    public Component getFormattedName(UnaryOperator<Style> style) {
        return this.name().copy().withStyle(style);
    }
}
