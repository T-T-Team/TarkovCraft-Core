package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.SkillContext;

import java.util.function.Function;

public interface SkillTracker {

    Codec<SkillTracker> CODEC = CoreRegistries.SKILL_TRIGGER_TYPE.byNameCodec()
            .dispatch(SkillTracker::codec, Function.identity());

    boolean isTriggerable(SkillContext context);

    float trigger(SkillContext context);

    MapCodec<? extends SkillTracker> codec();
}
