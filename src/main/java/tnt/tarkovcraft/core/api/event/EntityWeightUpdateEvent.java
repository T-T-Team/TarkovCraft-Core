package tnt.tarkovcraft.core.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;

public class EntityWeightUpdateEvent extends Event {

    private final LivingEntity entity;
    private final int originalWeight;
    private final int newWeight;
    private final float overweightFactor;

    public EntityWeightUpdateEvent(LivingEntity entity, int originalWeight, int newWeight, float overweightFactor) {
        this.entity = entity;
        this.originalWeight = originalWeight;
        this.newWeight = newWeight;
        this.overweightFactor = overweightFactor;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public int getOriginalWeight() {
        return originalWeight;
    }

    public int getNewWeight() {
        return newWeight;
    }

    public float getOverweightFactor() {
        return overweightFactor;
    }
}
