package tnt.tarkovcraft.core.common.pose;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;

public record EntityPoseType<T extends EntityPose>(Identifier identifier, MapCodec<T> codec) {

    public static final Codec<EntityPose> CODEC = CoreRegistries.ENTITY_POSE.byNameCodec()
            .dispatch(EntityPose::getType, EntityPoseType::codec);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EntityPoseType<?> that)) return false;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return this.identifier.toString();
    }
}
