package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTracker;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerType;

import java.util.List;

public final class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDefinition.CODEC.fieldOf("skill").forGetter(t -> t.definition),
            SkillTrackerType.INSTANCE_CODEC.listOf().fieldOf("trackers").forGetter(t -> t.trackers)
    ).apply(instance, Skill::new));

    private final Holder<SkillDefinition> definition;
    private final List<SkillTracker> trackers;

    Skill(Holder<SkillDefinition> definition, List<SkillTracker> trackers) {
        this.definition = definition;
        this.trackers = trackers;
    }
}
