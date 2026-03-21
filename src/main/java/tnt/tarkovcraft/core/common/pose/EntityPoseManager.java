package tnt.tarkovcraft.core.common.pose;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.CoreDataAttachments;

import java.util.function.Supplier;

public final class EntityPoseManager {

    public static final Marker MARKER = MarkerManager.getMarker("EntityPoseManager");

    public static boolean isInPose(LivingEntity entity, EntityPoseType<?> type) {
        EntityPose pose = getEntityPose(entity);
        return pose != null && pose.getType().equals(type);
    }

    public static boolean isInPose(LivingEntity entity, Holder<EntityPoseType<?>> type) {
        return isInPose(entity, type.value());
    }

    public static boolean isInPose(LivingEntity entity, Supplier<EntityPoseType<?>> type) {
        return isInPose(entity, type.get());
    }

    public static boolean isInPose(LivingEntity entity, EntityPose poseInstance) {
        return isInPose(entity, poseInstance.getType());
    }

    public static void setEntityPose(LivingEntity entity, EntityPose pose) {
        EntityPose currentPose = getEntityPose(entity);
        if (currentPose != null && currentPose.getType().equals(pose.getType()))
            return;
        clearEntityPose(entity);
        entity.setData(CoreDataAttachments.ENTITY_POSE, pose);
        TarkovCraftCore.LOGGER.debug(MARKER, "Changed pose to {} for entity {}", pose.getType(), entity);
        pose.onEnabled(entity);
    }

    public static void clearEntityPose(LivingEntity entity) {
        EntityPose oldPose = getEntityPose(entity);
        if (oldPose == null)
            return;
        EntityPose replacement = oldPose.onDisabled(entity);
        if (replacement != null) {
            entity.setData(CoreDataAttachments.ENTITY_POSE, replacement);
            TarkovCraftCore.LOGGER.debug(MARKER, "Replaced pose for entity {} with {}", entity, replacement.getType());
        } else {
            entity.removeData(CoreDataAttachments.ENTITY_POSE);
            TarkovCraftCore.LOGGER.debug(MARKER, "Removed pose for entity {}", entity);
        }
    }

    public static @Nullable EntityPose getEntityPose(LivingEntity entity) {
        return entity.getExistingDataOrNull(CoreDataAttachments.ENTITY_POSE);
    }

    public static boolean isTagged(LivingEntity entity, EntityPoseFlag flag) {
        EntityPose pose = getEntityPose(entity);
        return pose != null && pose.getFlags().contains(flag);
    }
}
