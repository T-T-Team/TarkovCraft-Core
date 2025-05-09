package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;

public record SkillTrackerType<T extends SkillTracker>(ResourceLocation identifier, MapCodec<T> codec) {

    public static final Codec<SkillTracker> CODEC = CoreRegistries.SKILL_TRIGGER_TYPE.byNameCodec().dispatch(SkillTracker::getType, SkillTrackerType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SkillTrackerType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }
}
