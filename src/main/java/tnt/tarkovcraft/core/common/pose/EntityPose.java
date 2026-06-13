package tnt.tarkovcraft.core.common.pose;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.init.CoreRegistries;

import java.util.Objects;
import java.util.Set;

public interface EntityPose {

    void onEnabled(LivingEntity entity);

    EntityPose onDisabled(LivingEntity entity);

    Set<EntityPoseFlag> getFlags();

    Type<?> getType();

    record Type<T extends EntityPose>(ResourceLocation identifier, MapCodec<T> codec) {

        public static final Codec<EntityPose> CODEC = CoreRegistries.ENTITY_POSE.byNameCodec()
                .dispatch(EntityPose::getType, Type::codec);

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Type<?> that)) return false;
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
}
