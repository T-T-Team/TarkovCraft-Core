package tnt.tarkovcraft.core.common.pose;

import com.mojang.serialization.MapCodec;
import tnt.tarkovcraft.core.common.init.CoreEntityPoses;

import java.util.Collections;
import java.util.Set;

public final class NoEntityPose extends EntityStatusPose {

    private static final NoEntityPose INSTANCE = new NoEntityPose();
    public static final MapCodec<NoEntityPose> CODEC = MapCodec.unit(INSTANCE);

    private NoEntityPose() {
    }

    public static EntityPose instance() {
        return INSTANCE;
    }

    @Override
    public Set<EntityPoseFlag> getFlags() {
        return Collections.emptySet();
    }

    @Override
    public EntityPoseType<?> getType() {
        return CoreEntityPoses.NO_POSE.get();
    }
}
