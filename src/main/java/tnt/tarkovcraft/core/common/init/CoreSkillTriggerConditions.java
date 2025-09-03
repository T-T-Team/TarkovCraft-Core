package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.condition.*;

import java.util.function.Supplier;

public final class CoreSkillTriggerConditions {

    public static final DeferredRegister<SkillTriggerConditionType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.Keys.SKILL_TRIGGER_CONDITION_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTriggerConditionType<NotSkillTriggerCondition>> NOT = REGISTRY.register("not", key -> new SkillTriggerConditionType<>(key, NotSkillTriggerCondition.CODEC));
    public static final Supplier<SkillTriggerConditionType<IsSprintingSkillTriggerCondition>> IS_SPRINTING = REGISTRY.register("sprinting", key -> new SkillTriggerConditionType<>(key, IsSprintingSkillTriggerCondition.CODEC));
    public static final Supplier<SkillTriggerConditionType<ConfigToggleSkillTriggerCondition>> CONFIG_TOGGLE = REGISTRY.register("configurable", key -> new SkillTriggerConditionType<>(key, ConfigToggleSkillTriggerCondition.CODEC));
    public static final Supplier<SkillTriggerConditionType<IsOverweightSkillTriggerCondition>> IS_OVERWEIGHT = REGISTRY.register("overweight", key -> new SkillTriggerConditionType<>(key, IsOverweightSkillTriggerCondition.CODEC));
}
