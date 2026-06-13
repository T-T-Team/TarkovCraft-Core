package tnt.tarkovcraft.core.common.skill.tracker.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.init.CoreRegistries;
import tnt.tarkovcraft.core.common.skill.SkillContext;

import java.util.function.Function;

public interface SkillTriggerCondition {

    Codec<SkillTriggerCondition> CODEC = CoreRegistries.SKILL_TRIGGER_CONDITION_TYPE.byNameCodec()
            .dispatch(SkillTriggerCondition::codec, Function.identity());

    boolean isTriggerable(SkillContext context);

    Component getDescription();

    MapCodec<? extends SkillTriggerCondition> codec();
}
