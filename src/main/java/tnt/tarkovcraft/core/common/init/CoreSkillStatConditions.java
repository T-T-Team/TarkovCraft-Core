package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.stat.condition.IsMaxSkillLevelStatCondition;
import tnt.tarkovcraft.core.common.skill.stat.condition.IsSkillLevelRangeStatCondition;
import tnt.tarkovcraft.core.common.skill.stat.condition.SkillStatConditionType;

import java.util.function.Supplier;

public final class CoreSkillStatConditions {

    public static final DeferredRegister<SkillStatConditionType<?>> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.SKILL_STAT_CONDITION_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillStatConditionType<IsMaxSkillLevelStatCondition>> MAX_SKILL_LEVEL = REGISTRY.register("is_max_skill_level", key -> new SkillStatConditionType<>(key, IsMaxSkillLevelStatCondition.CODEC));
    public static final Supplier<SkillStatConditionType<IsSkillLevelRangeStatCondition>> SKILL_LEVEL_RANGE = REGISTRY.register("skill_level_range", key -> new SkillStatConditionType<>(key, IsSkillLevelRangeStatCondition.CODEC));
}
