package tnt.tarkovcraft.core.common.pose;

import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public interface EntityPose {

    void onEnabled(LivingEntity entity);

    EntityPose onDisabled(LivingEntity entity);

    Set<EntityPoseFlag> getFlags();

    EntityPoseType<?> getType();
}
