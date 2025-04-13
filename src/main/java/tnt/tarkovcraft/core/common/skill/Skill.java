package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTracker;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerType;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDefinition.CODEC.fieldOf("skill").forGetter(t -> t.definition),
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, SkillTrackerType.DATA_CODEC).fieldOf("trackerData").forGetter(t -> t.trackerData),
            Codec.INT.fieldOf("level").forGetter(t -> t.level),
            Codec.FLOAT.fieldOf("exp").forGetter(t -> t.experience),
            Codec.FLOAT.fieldOf("requiredExp").forGetter(t -> t.requiredExperience)
    ).apply(instance, Skill::new));

    private final Holder<SkillDefinition> definition;
    private final Map<UUID, SkillTracker> trackerData;
    private int level;
    private float experience;
    private float requiredExperience;

    Skill(Holder<SkillDefinition> definition, Map<UUID, SkillTracker> trackerData, int level, float experience, float requiredExperience) {
        this.definition = definition;
        this.trackerData = new HashMap<>(trackerData);
        this.level = level;
        this.experience = experience;
        this.requiredExperience = requiredExperience;
    }

    public Skill(Holder<SkillDefinition> definition) {
        this(definition, Collections.emptyMap(), 0, 0.0F, definition.value().getLevelDefinition().getRequiredExperience(0));
    }

    public float trigger(Context context) {
        float triggeredAmount = 0;
        for (SkillTrackerDefinition trackerDefinition : this.definition.value().getTrackers()) {
            UUID id = trackerDefinition.id();
            SkillTracker tracker = this.trackerData.computeIfAbsent(id, key -> trackerDefinition.createTracker());
            triggeredAmount += trackerDefinition.trigger(context, tracker);
        }
        return triggeredAmount;
    }

    public void addExperience(float experience, Runnable levelChangeCallback) {
        if (this.isMaxLevel())
            return;
        if ((this.experience += experience) >= this.requiredExperience) {
            this.experience = this.experience - this.requiredExperience;
            this.level++;
            SkillLevelDefinition levelDefinition = this.definition.value().getLevelDefinition();
            this.requiredExperience = levelDefinition.getRequiredExperience(this.level);
            levelChangeCallback.run();
        }
    }

    public int getLevel() {
        return level;
    }

    public float getExperience() {
        return experience;
    }

    public float getRequiredExperience() {
        return requiredExperience;
    }

    public boolean isMaxLevel() {
        SkillLevelDefinition levelDefinition = this.definition.value().getLevelDefinition();
        return this.level >= levelDefinition.getMaxLevel();
    }

    public Holder<SkillDefinition> getDefinition() {
        return definition;
    }
}
