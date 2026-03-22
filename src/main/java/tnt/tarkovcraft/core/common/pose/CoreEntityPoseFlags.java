package tnt.tarkovcraft.core.common.pose;

import tnt.tarkovcraft.core.TarkovCraftCore;

public final class CoreEntityPoseFlags {

    public static final EntityPoseFlag NO_INTERACTION = new EntityPoseFlag(TarkovCraftCore.createResourceLocation("pose_flag/no_interaction"));
    public static final EntityPoseFlag NO_MOVEMENT = new EntityPoseFlag(TarkovCraftCore.createResourceLocation("pose_flag/no_movement"));
    public static final EntityPoseFlag NO_KNOCKBACK = new EntityPoseFlag(TarkovCraftCore.createResourceLocation("pose_flag/no_knockback"));
}
