package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTracker;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerDefinition;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTrackerType;
import tnt.tarkovcraft.core.util.context.Context;

import java.util.*;
import java.util.function.IntConsumer;

public final class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDefinition.CODEC.fieldOf("skill").forGetter(t -> t.definition),
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, SkillTrackerType.DATA_CODEC).fieldOf("trackerData").forGetter(t -> t.trackerData),
            Codec.INT.fieldOf("level").forGetter(t -> t.level),
            Codec.FLOAT.fieldOf("exp").forGetter(t -> t.experience),
            Codec.FLOAT.fieldOf("requiredExp").forGetter(t -> t.requiredExperience),
            Codec.LONG.fieldOf("lastExpUpdate").forGetter(t -> t.lastExperienceUpdate)
    ).apply(instance, Skill::new));
    public static final Component MAX_LEVEL = Component.translatable("label.tarkovcraft_core.skill.max_level").withStyle(ChatFormatting.GOLD);

    private final Holder<SkillDefinition> definition;
    private final Map<UUID, SkillTracker> trackerData;
    private int level;
    private float experience;
    private float requiredExperience;
    private long lastExperienceUpdate;

    Skill(Holder<SkillDefinition> definition, Map<UUID, SkillTracker> trackerData, int level, float experience, float requiredExperience, long lastExperienceUpdate) {
        this.definition = definition;
        this.trackerData = new HashMap<>(trackerData);
        this.level = level;
        this.experience = experience;
        this.requiredExperience = requiredExperience;
        this.lastExperienceUpdate = lastExperienceUpdate;
    }

    public Skill(Holder<SkillDefinition> definition) {
        this(definition, Collections.emptyMap(), 0, 0.0F, definition.value().getLevelDefinition().getRequiredExperience(0), 0L);
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

    public void updateMemory(long time, EntityAttributeData attributeData, IntConsumer levelChangeCallback) {
        SkillMemoryConfiguration memory = this.definition.value().getMemory();
        if (SkillSystem.isMemoryEnabled() && memory.isEnabled()) {
            long diff = time - this.lastExperienceUpdate;
            float rateMultiplier = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_TIME_MULTIPLIER).floatValue();
            long timeToForget = (long) (memory.getForgetAfter() * rateMultiplier);
            long times = diff / timeToForget;
            if (times > 0) {
                long additional = diff % timeToForget;
                float amountMultiplier = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_AMOUNT_MULTIPLIER).floatValue();
                float amount = memory.getForgetAmount() * amountMultiplier * times;
                this.loseExperience(amount, memory, levelChangeCallback);
                time -= additional;
            }
        }
        this.setLastExperienceUpdate(time);
    }

    public void loseExperience(float experience, SkillMemoryConfiguration memoryConfig, IntConsumer levelChangeCallback) {
        float experienceToLose = experience > this.experience && !memoryConfig.canLoseLevel() ? this.experience : experience;
        float currentLoss = Math.min(experienceToLose, this.experience);
        float overflow = experienceToLose - currentLoss;
        this.experience -= currentLoss;
        if (SkillSystem.isLevelMemoryEnabled() && overflow > 0 && this.level > 0) {
            this.level--;
            this.requiredExperience = this.definition.value().getLevelDefinition().getRequiredExperience(this.level);
            this.experience = this.requiredExperience;
            levelChangeCallback.accept(this.level + 1);
            this.loseExperience(overflow, memoryConfig, levelChangeCallback);
        }
    }

    public void setLastExperienceUpdate(long lastExperienceUpdate) {
        this.lastExperienceUpdate = lastExperienceUpdate;
    }

    public void addExperience(float experience, IntConsumer levelChangeCallback) {
        if (this.isMaxLevel() || !this.definition.value().isEnabled())
            return;
        if ((this.experience += experience) >= this.requiredExperience) {
            this.level++;
            float overflow = this.experience - this.requiredExperience;
            SkillLevelDefinition levelDefinition = this.definition.value().getLevelDefinition();
            this.requiredExperience = levelDefinition.getRequiredExperience(this.level);
            this.experience = 0.0F;
            levelChangeCallback.accept(this.level - 1);
            this.addExperience(overflow, levelChangeCallback);
        }
    }

    public void forceSetLevel(int level) {
        this.level = level;
        this.experience = 0;
        this.requiredExperience = this.definition.value().getLevelDefinition().getRequiredExperience(this.level);
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return this.definition.value().getLevelDefinition().getMaxLevel();
    }

    public float getExperience() {
        return experience;
    }

    public float getRequiredExperience() {
        return requiredExperience;
    }

    public boolean isMaxLevel() {
        return this.level >= this.getMaxLevel();
    }

    public Holder<SkillDefinition> getDefinition() {
        return definition;
    }
}
