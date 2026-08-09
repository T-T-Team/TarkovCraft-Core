package tnt.tarkovcraft.core.common.skill.bonus.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

import java.util.function.Function;

public interface SkillStatCondition {

    Codec<SkillStatCondition> CODEC = CoreRegistries.SKILL_STAT_CONDITION_TYPE.byNameCodec()
            .dispatch(SkillStatCondition::codec, Function.identity());

    boolean canApply(SkillDefinition definition, Skill skill, Entity entity);

    MapCodec<? extends SkillStatCondition> codec();
}
