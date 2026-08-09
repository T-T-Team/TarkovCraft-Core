package tnt.tarkovcraft.core.common.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;
import tnt.tarkovcraft.core.common.init.CoreAttributes;
import tnt.tarkovcraft.core.common.skill.progression.SkillProgressionStrategy;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerDefinition;

public final class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SkillDefinition.CODEC.fieldOf("skill").forGetter(t -> t.definition),
            Codec.INT.fieldOf("level").forGetter(t -> t.level),
            Codec.FLOAT.fieldOf("exp").forGetter(t -> t.experience),
            Codec.FLOAT.fieldOf("requiredExp").forGetter(t -> t.requiredExperience),
            Codec.LONG.fieldOf("lastExpUpdate").forGetter(t -> t.lastExperienceUpdate)
    ).apply(instance, Skill::new));
    public static final Component MAX_LEVEL = Component.translatable("label.tarkovcraft_core.skill.max_level").withStyle(ChatFormatting.GOLD);

    private final Holder<SkillDefinition> definition;
    private int level;
    private float experience;
    private float requiredExperience;
    private long lastExperienceUpdate;

    private LevelChangeListener levelChangeListener = LevelChangeListener.NO_OP;

    Skill(Holder<SkillDefinition> definition, int level, float experience, float requiredExperience, long lastExperienceUpdate) {
        this.definition = definition;
        this.level = level;
        this.experience = experience;
        this.requiredExperience = requiredExperience;
        this.lastExperienceUpdate = lastExperienceUpdate;
    }

    public Skill(Holder<SkillDefinition> definition) {
        this(definition, 0, 0.0F, definition.value().configuration().progressionStrategy().getRequiredExperience(0), 0L);
    }

    public void setLevelChangeListener(LevelChangeListener levelChangeListener) {
        this.levelChangeListener = levelChangeListener;
    }

    public float trigger(SkillContext context) {
        float triggeredAmount = 0;
        SkillSystemConfig config = TarkovCraftCore.getConfig().skillSystemConfig;
        float globalMultiplier = config.globalSkillLevelSpeedMultiplier;
        float limit = config.singleTriggerSkillLevelLimit <= 0.0F ? Float.MAX_VALUE : config.singleTriggerSkillLevelLimit;
        for (SkillTriggerDefinition triggerDefinition : this.definition.value().triggers()) {
            if (triggerDefinition.isTriggerable(context)) {
                triggeredAmount += triggerDefinition.trigger(context);
            }
        }
        return Math.min(triggeredAmount * globalMultiplier, limit);
    }

    public void updateMemory(long time, EntityAttributeData attributeData) {
        SkillMemoryConfiguration memory = this.definition.value().configuration().memory();
        if (SkillSystem.isMemoryEnabled() && memory.isEnabled()) {
            long diff = time - this.lastExperienceUpdate;
            float rateMultiplier = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_TIME_MULTIPLIER).floatValue();
            long timeToForget = (long) (memory.startAfter() * rateMultiplier);
            long times = diff / timeToForget;
            if (times > 0) {
                long additional = diff % timeToForget;
                float amountMultiplier = attributeData.getAttribute(CoreAttributes.MEMORY_FORGET_AMOUNT_MULTIPLIER).floatValue();
                float amount = memory.experienceLoss() * amountMultiplier * times;
                this.loseExperience(amount, memory);
                time -= additional;
            }
        }
        this.setLastExperienceUpdate(time);
    }

    public void loseExperience(float experience, SkillMemoryConfiguration memoryConfig) {
        float experienceToLose = experience > this.experience && !memoryConfig.canLoseLevel() ? this.experience : experience;
        float currentLoss = Math.min(experienceToLose, this.experience);
        float overflow = experienceToLose - currentLoss;
        this.experience -= currentLoss;
        if (SkillSystem.isLevelMemoryEnabled() && overflow > 0 && this.level > 0) {
            this.level--;
            SkillProgressionStrategy progressionStrategy = this.definition.value().configuration().progressionStrategy();
            this.requiredExperience = progressionStrategy.getRequiredExperience(this.level);
            this.experience = this.requiredExperience;
            this.levelChangeListener.onLevelChanged(this, this.level, this.level + 1);
            this.loseExperience(overflow, memoryConfig);
        }
    }

    public void setLastExperienceUpdate(long lastExperienceUpdate) {
        this.lastExperienceUpdate = lastExperienceUpdate;
    }

    public void addExperience(float experience) {
        if (this.isMaxLevel())
            return;
        if ((this.experience += experience) >= this.requiredExperience) {
            this.level++;
            float overflow = this.experience - this.requiredExperience;
            SkillProgressionStrategy progressionStrategy = this.definition.value().configuration().progressionStrategy();
            this.requiredExperience = progressionStrategy.getRequiredExperience(this.level);
            this.experience = 0.0F;
            this.levelChangeListener.onLevelChanged(this, this.level, this.level - 1);
            if (this.level < this.getMaxLevel()) {
                this.addExperience(overflow);
            }
        }
    }

    public void forceSetLevel(int level) {
        this.level = level;
        this.experience = 0;
        this.requiredExperience = this.getRequiredExperienceForLevel(this.level);
    }

    public int getLevel() {
        return this.level;
    }

    public int getMaxLevel() {
        return this.definition.value().configuration().maxLevel();
    }

    public float getExperience() {
        return this.experience;
    }

    public float getRequiredExperience() {
        return this.requiredExperience;
    }

    public float getRequiredExperienceForLevel(int level) {
        return this.definition.value().configuration().progressionStrategy().getRequiredExperience(level);
    }

    public boolean isMaxLevel() {
        return this.level >= this.getMaxLevel();
    }

    public Holder<SkillDefinition> getDefinition() {
        return definition;
    }

    @FunctionalInterface
    public interface LevelChangeListener {
        LevelChangeListener NO_OP = (skill, level, prevLevel) -> {};
        void onLevelChanged(Skill skill, int currentLevel, int previousLevel);
    }
}
