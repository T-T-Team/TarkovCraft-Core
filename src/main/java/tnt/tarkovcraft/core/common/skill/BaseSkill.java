package tnt.tarkovcraft.core.common.skill;

import net.minecraft.util.Mth;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.AdditionalDatapackInput;
import tnt.tarkovcraft.core.common.config.SkillSystemConfig;

public class BaseSkill implements Skill, AdditionalDatapackInput<ExternalSkillData> {

    private final SkillType<?> type;
    private boolean enabled;
    private boolean canForgetExperience;
    private boolean canForgetLevel;
    private int maxLevel;
    private int level;
    private int experience;
    private int experienceRequired;
    private float experienceLevelFactor;

    public BaseSkill(SkillType<?> type) {
        this.type = type;
    }

    @Override
    public void addExperience(int experience) {
        if (!this.enabled || !this.isLevelable())
            return;
        if (experience < 0 && this.canForgetExperience) {
            int newExperience = this.experience + experience;
            this.experience = Math.max(0, newExperience);
            if (newExperience < 0 && this.canForgetLevel && this.level > 0) {
                int additionalLoss = Math.abs(newExperience);
                this.level--;
                this.experience = this.getMaxExperience();
                this.addExperience(-additionalLoss);
            }
        } else if (experience > 0) {
            int requiredExperience = this.getMaxExperience();
            int rollOverExperience = this.getExperience() + experience - requiredExperience;
            if (rollOverExperience > 0) {
                this.level++;
                this.addExperience(rollOverExperience);
            }
        }
    }

    @Override
    public int getSkillLevel() {
        return this.enabled ? this.level : 0;
    }

    @Override
    public int getExperience() {
        return this.enabled ? this.experience : 0;
    }

    @Override
    public int getMaxExperience() {
        return Mth.ceil(this.experienceRequired * ((1 + this.level) * this.experienceLevelFactor));
    }

    @Override
    public boolean isLevelable() {
        return this.level < this.maxLevel;
    }

    @Override
    public SkillType<?> getType() {
        return this.type;
    }

    @Override
    public void onDatapackInput(ExternalSkillData data) {
        SkillSystemConfig config = TarkovCraftCore.getConfig().skillSystemConfig;
        this.enabled = config.skillSystemEnabled && data.enabled();
        this.maxLevel = data.maxLevel();
        this.experienceRequired = data.baseExperience();
        this.experienceLevelFactor = data.levelingFactor();
        this.canForgetExperience = config.enableSkillLoss && data.canLoseExperience();
        this.canForgetLevel = config.enableSkillLoss && data.canLoseLevel();
    }
}
