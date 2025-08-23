package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.weight.WeightSystem;
import tnt.tarkovcraft.core.util.context.Context;
import tnt.tarkovcraft.core.util.context.ContextKeys;

public class OverweightFactorSkillTracker implements SkillTracker {

    public static final MapCodec<OverweightFactorSkillTracker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("value").forGetter(t -> t.value),
            ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("limit", 2.5F).forGetter(t -> t.limit),
            Codec.BOOL.optionalFieldOf("countOverLimit", false).forGetter(t -> t.countOverLimit)
    ).apply(instance, OverweightFactorSkillTracker::new));

    private final float value;
    private final float limit;
    private final boolean countOverLimit;

    public OverweightFactorSkillTracker(float value, float limit, boolean countOverLimit) {
        this.value = value;
        this.limit = limit;
        this.countOverLimit = countOverLimit;
    }

    @Override
    public boolean isTriggerable(Context context) {
        return context.get(ContextKeys.ENTITY)
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> WeightSystem.isOverweight((LivingEntity) entity))
                .orElse(false);
    }

    @Override
    public float trigger(Context context) {
        Entity entity = context.getOrThrow(ContextKeys.ENTITY);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return 0.0F;
        }
        float factor = WeightSystem.getOverweightEffectFactor(livingEntity);
        if (factor <= 0.0) {
            return 0.0F;
        }
        if (factor >= this.limit) {
            return this.countOverLimit ? this.value : 0.0F;
        }
        return this.value * (1.0F - factor / this.limit);
    }

    @Override
    public SkillTrackerType<?> getType() {
        return CoreSkillTrackers.OVERWEIGHT_FACTOR.get();
    }
}
