package tnt.tarkovcraft.core.common.skill.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.TarkovCraftRegistries;

public record SkillTrackerType<T extends SkillTracker>(ResourceLocation identifier, MapCodec<T> codec) {

    public static final Codec<SkillTracker> INSTANCE_CODEC = TarkovCraftRegistries.SKILL_TRIGGER_TYPE.byNameCodec().dispatch(SkillTracker::getType, SkillTrackerType::codec);
}
