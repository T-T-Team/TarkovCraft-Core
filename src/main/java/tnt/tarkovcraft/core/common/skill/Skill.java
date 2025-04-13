package tnt.tarkovcraft.core.common.skill;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.skill.trigger.SkillSaveData;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTracker;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrackerType;
import tnt.tarkovcraft.core.common.skill.trigger.condition.SkillTriggerCondition;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDefinition.CODEC.fieldOf("skill").forGetter(t -> t.definition),
            SkillTrackerData.CODEC.listOf().fieldOf("trackers").forGetter(t -> t.trackers)
    ).apply(instance, Skill::new));

    private final Holder<SkillDefinition> definition;
    private final List<SkillTrackerData> trackers;
    private final SkillSaveData skillSaveData;
    private int level;
    private float experience;
    private float requiredExperience;

    Skill(Holder<SkillDefinition> definition, List<SkillTrackerData> trackers) {
        this.definition = definition;
        this.trackers = trackers;
        this.skillSaveData = new SkillSaveData();
    }

    private <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
        RecordBuilder<T> builder = ops.mapBuilder();
        builder.add("definition", ops.withEncoder(SkillDefinition.CODEC).apply(this.definition));
        builder.add("trackers", ops.withEncoder(SkillTrackerData.CODEC.listOf()).apply(this.trackers));
        builder.add("saveData", this.skillSaveData.encode(ops, prefix, this.trackers));
        return builder.build(prefix);
    }

    private static <T, R> DataResult<Pair<R, T>> decode(DynamicOps<T> ops, T input) {
        MapLike<T> map = ops.getMap(input).getOrThrow();
        var definition = SkillDefinition.CODEC.parse(ops, map.get("definition"));
        var trackers = SkillTrackerData.CODEC.listOf().parse(ops, map.get("trackers"));
        return DataResult.success(Pair.of(null, input));
    }

    public float trigger(Context context) {
        float triggeredAmount = 0;
        for (SkillTrackerData trackerData : this.trackers) {
            List<SkillTriggerCondition> conditions = this.definition.value().getTrackerConditions(trackerData.id());
            for (SkillTriggerCondition condition : conditions) {
                if (!condition.isTriggerable(context)) {
                    return 0.0F;
                }
            }
            SkillTracker tracker = trackerData.tracker();
            if (tracker.isTriggerable(context)) {
                triggeredAmount += tracker.trigger(context);
            }
        }
        return triggeredAmount;
    }

    public void addExperience(float experience, Runnable levelChangeCallback) {
        if ((this.experience += experience) >= this.requiredExperience) {
            this.experience = this.requiredExperience - this.experience;
            this.level++;
            this.requiredExperience = 10.0F + this.level * 15.0F;
            levelChangeCallback.run();
        }
    }

    public boolean isMaxLevel() {
        return this.level == 100;
    }

    public Holder<SkillDefinition> getDefinition() {
        return definition;
    }

    public record SkillTrackerData(UUID id, SkillTracker tracker) {

        public static final Codec<SkillTrackerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(SkillTrackerData::id),
                SkillTrackerType.INSTANCE_CODEC.fieldOf("data").forGetter(SkillTrackerData::tracker)
        ).apply(instance, SkillTrackerData::new));

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SkillTrackerData that)) return false;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}
