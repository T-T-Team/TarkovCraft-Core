package tnt.tarkovcraft.core.common.skill.stat.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.data.number.NumberProvider;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

public record IsSkillLevelRangeStatCondition(int min, int max) implements SkillStatCondition {

    public static final MapCodec<IsSkillLevelRangeStatCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.POSITIVE_INT.optionalFieldOf("min", 0).forGetter(t -> t.min),
            NumberProvider.POSITIVE_INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(t -> t.max)
    ).apply(instance, IsSkillLevelRangeStatCondition::new));

    @Override
    public boolean canApply(SkillDefinition definition, Skill skill, Entity entity) {
        int level = skill.getLevel();
        int from = Math.min(this.min, this.max);
        int to = Math.max(this.max, this.min);
        return level >= from && level <= to;
    }

    @Override
    public MapCodec<? extends SkillStatCondition> codec() {
        return CODEC;
    }
}
