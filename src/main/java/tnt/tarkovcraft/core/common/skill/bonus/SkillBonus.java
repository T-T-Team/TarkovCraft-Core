package tnt.tarkovcraft.core.common.skill.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.Skill;
import tnt.tarkovcraft.core.common.skill.SkillDefinition;

import java.util.function.Function;

public interface SkillBonus {

    Codec<SkillBonus> CODEC = CoreRegistries.SKILL_STAT.byNameCodec()
            .dispatch(SkillBonus::codec, Function.identity());

    void apply(SkillDefinition definition, Skill skill, Entity entity);

    void clear(SkillDefinition definition, Skill skill, Entity entity);

    Object[] getTranslationData(SkillDefinition definition, Skill skill, Entity entity);

    MapCodec<? extends SkillBonus> codec();
}
