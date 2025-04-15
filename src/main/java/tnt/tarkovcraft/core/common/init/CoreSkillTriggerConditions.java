package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.condition.IsSprintingSkillTriggerCondition;
import tnt.tarkovcraft.core.common.skill.tracker.condition.SkillTriggerConditionType;

import java.util.function.Supplier;

public final class CoreSkillTriggerConditions {

    public static final DeferredRegister<SkillTriggerConditionType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.SKILL_TRIGGER_CONDITION_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTriggerConditionType<IsSprintingSkillTriggerCondition>> IS_SPRINTING = REGISTRY.register("sprinting", key -> new SkillTriggerConditionType<>(key, IsSprintingSkillTriggerCondition.CODEC));
}
