package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerType;
import tnt.tarkovcraft.core.common.skill.tracker.SimpleSkillTracker;

import java.util.function.Supplier;

public final class CoreSkillTrackers {

    public static final DeferredRegister<SkillTrackerType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.SKILL_TRIGGER_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTrackerType<SimpleSkillTracker>> SIMPLE = REGISTRY.register("simple", key -> new SkillTrackerType<>(key, SimpleSkillTracker.CODEC));
}
