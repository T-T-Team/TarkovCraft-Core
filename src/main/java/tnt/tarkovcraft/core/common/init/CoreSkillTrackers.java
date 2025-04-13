package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.LimitedSkillTrackerConfigurationConfiguration;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerType;
import tnt.tarkovcraft.core.common.skill.tracker.UnrestrictedSkillTrackerConfigurationConfiguration;

import java.util.function.Supplier;

public final class CoreSkillTrackers {

    public static final DeferredRegister<SkillTrackerType<?, ?>> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.SKILL_TRIGGER_TYPE, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTrackerType<UnrestrictedSkillTrackerConfigurationConfiguration.UnrestrictedDataHolder, UnrestrictedSkillTrackerConfigurationConfiguration>> UNRESTRICTED = REGISTRY.register("unrestricted", key -> new SkillTrackerType<>(key, UnrestrictedSkillTrackerConfigurationConfiguration.CODEC, UnrestrictedSkillTrackerConfigurationConfiguration.UnrestrictedDataHolder.CODEC));
    public static final Supplier<SkillTrackerType<LimitedSkillTrackerConfigurationConfiguration.Tracker, LimitedSkillTrackerConfigurationConfiguration>> LIMITED = REGISTRY.register("limited", key -> new SkillTrackerType<>(key, LimitedSkillTrackerConfigurationConfiguration.CODEC, LimitedSkillTrackerConfigurationConfiguration.Tracker.CODEC));
}
