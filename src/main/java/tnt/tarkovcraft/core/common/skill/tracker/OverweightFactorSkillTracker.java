package tnt.tarkovcraft.core.common.skill.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.init.CoreSkillTrackers;
import tnt.tarkovcraft.core.common.skill.SkillContext;
import tnt.tarkovcraft.core.common.weight.WeightSystem;

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
    public boolean isTriggerable(SkillContext context) {
        Entity entity = context.entity();
        return entity instanceof LivingEntity livingEntity && WeightSystem.isOverweight(livingEntity);
    }

    @Override
    public float trigger(SkillContext context) {
        LivingEntity entity = context.asLivingEntity();
        if (entity == null) {
            return 0.0F;
        }
        float factor = WeightSystem.getOverweightEffectFactor(entity);
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
