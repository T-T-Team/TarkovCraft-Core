package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

public record SkillTrackerType<T extends SkillTracker, CFG extends SkillTrackerConfiguration<T>>(ResourceLocation identifier, MapCodec<CFG> codec, MapCodec<T> dataCodec) {

    public static final Codec<SkillTrackerConfiguration<?>> CONFIGURATION_CODEC = CoreRegistries.SKILL_TRIGGER_TYPE.byNameCodec().dispatch(SkillTrackerConfiguration::getType, SkillTrackerType::codec);
    public static final Codec<SkillTracker> DATA_CODEC = CoreRegistries.SKILL_TRIGGER_TYPE.byNameCodec().dispatch(SkillTracker::getType, SkillTrackerType::dataCodec);
}
