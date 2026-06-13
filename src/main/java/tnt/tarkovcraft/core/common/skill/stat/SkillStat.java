package tnt.tarkovcraft.core.common.skill.stat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

import java.util.function.Function;

public interface SkillStat {

    Codec<SkillStat> CODEC = CoreRegistries.SKILL_STAT.byNameCodec()
            .dispatch(SkillStat::codec, Function.identity());

    void apply(SkillDefinition definition, Skill skill, Entity entity);

    void clear(SkillDefinition definition, Skill skill, Entity entity);

    Object[] getTranslationData(SkillDefinition definition, Skill skill, Entity entity);

    MapCodec<? extends SkillStat> codec();
}
