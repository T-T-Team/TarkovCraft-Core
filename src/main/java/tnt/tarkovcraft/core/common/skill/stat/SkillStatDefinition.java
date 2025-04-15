package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.skill.stat.condition.SkillStatCondition;
import tnt.tarkovcraft.core.common.skill.stat.condition.SkillStatConditionType;
import tnt.tarkovcraft.core.util.Codecs;

import java.util.Collections;
import java.util.List;

public record SkillStatDefinition(List<SkillStatCondition> conditions, int badgeColor, ResourceLocation badgeIcon, SkillStat stat) {

    public static final Codec<SkillStatDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillStatConditionType.INSTANCE_CODEC.listOf().optionalFieldOf("conditions", Collections.emptyList()).forGetter(SkillStatDefinition::conditions),
            Codecs.RGB_COLOR.fieldOf("badgeColor").forGetter(SkillStatDefinition::badgeColor),
            ResourceLocation.CODEC.fieldOf("badgeIcon").forGetter(SkillStatDefinition::badgeIcon),
            SkillStatType.INSTANCE_CODEC.fieldOf("apply").forGetter(SkillStatDefinition::stat)
    ).apply(instance, SkillStatDefinition::new));
}
