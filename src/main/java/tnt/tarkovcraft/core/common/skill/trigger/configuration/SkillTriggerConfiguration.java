package tnt.tarkovcraft.core.common.skill.trigger.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.SkillContext;

import java.util.function.Function;

public interface SkillTriggerConfiguration {

    Codec<SkillTriggerConfiguration> CODEC = CoreRegistries.SKILL_TRIGGER_TYPE.byNameCodec()
            .dispatch(SkillTriggerConfiguration::codec, Function.identity());

    boolean canTrigger(SkillContext context);

    float getTriggerExperience(SkillContext context);

    MapCodec<? extends SkillTriggerConfiguration> codec();
}
