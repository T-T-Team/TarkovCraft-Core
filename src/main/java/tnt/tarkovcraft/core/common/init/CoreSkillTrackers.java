package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.trigger.LimitedSkillTracker;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerType;

import java.util.function.Supplier;

public final class CoreSkillTrackers {

    public static final DeferredRegister<SkillTrackerType<?>> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.SKILL_TRIGGER_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTrackerType<LimitedSkillTracker>> LIMITED = REGISTRY.register("limited", key -> new SkillTrackerType<>(key, LimitedSkillTracker.CODEC));
}
