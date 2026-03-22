package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.pose.EntityPoseType;
import tnt.tarkovcraft.core.common.pose.NoEntityPose;

import java.util.function.Supplier;

public final class CoreEntityPoses {

    public static final DeferredRegister<EntityPoseType<?>> REGISTRY = DeferredRegister.create(CoreRegistries.Keys.ENTITY_POSE, TarkovCraftCore.MOD_ID);

    public static final Supplier<EntityPoseType<NoEntityPose>> NO_POSE = REGISTRY.register("no_pose", key -> new EntityPoseType<>(key, NoEntityPose.CODEC));
}
