package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.LimitedSkillTrackerConfiguration;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerType;
import tnt.tarkovcraft.core.common.skill.tracker.UnrestrictedSkillTrackerConfiguration;

import java.util.function.Supplier;

public final class CoreSkillTrackers {

    public static final DeferredRegister<SkillTrackerType<?, ?>> REGISTRY = DeferredRegister.create(CoreRegistries.SKILL_TRIGGER_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTrackerType<UnrestrictedSkillTrackerConfiguration.UnrestrictedDataHolder, UnrestrictedSkillTrackerConfiguration>> UNRESTRICTED = REGISTRY.register("unrestricted", key -> new SkillTrackerType<>(key, UnrestrictedSkillTrackerConfiguration.CODEC, UnrestrictedSkillTrackerConfiguration.UnrestrictedDataHolder.CODEC));
    public static final Supplier<SkillTrackerType<LimitedSkillTrackerConfiguration.Tracker, LimitedSkillTrackerConfiguration>> LIMITED = REGISTRY.register("limited", key -> new SkillTrackerType<>(key, LimitedSkillTrackerConfiguration.CODEC, LimitedSkillTrackerConfiguration.Tracker.CODEC));
}
