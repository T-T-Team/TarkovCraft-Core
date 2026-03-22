package tnt.tarkovcraft.core.common.pose;

import net.minecraft.world.entity.LivingEntity;

public abstract class EntityStatusPose implements EntityPose {

    protected EntityStatusPose() {}

    @Override
    public final void onEnabled(LivingEntity entity) {
    }

    @Override
    public final EntityPose onDisabled(LivingEntity entity) {
        return null;
    }
}
