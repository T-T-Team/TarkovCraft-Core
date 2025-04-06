package tnt.tarkovcraft.core.common.skill;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractSkill {

    private final ResourceLocation identifier;
    private final int maxLevel;

    public AbstractSkill(ResourceLocation identifier, int maxLevel) {
        this.identifier = identifier;
        this.maxLevel = maxLevel;
    }

    public abstract boolean isSkillEnabled();

    public abstract int getRequiredExperience(int skillLevel);

    public boolean isMaxLevel(SkillTracker tracker) {
        return tracker.getLevel() >= this.getMaxLevel();
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }
}
