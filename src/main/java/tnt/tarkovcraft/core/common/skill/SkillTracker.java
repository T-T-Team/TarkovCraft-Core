package tnt.tarkovcraft.core.common.skill;

public final class SkillTracker {

    private final AbstractSkill skill;
    private int level;
    private int experience;

    public SkillTracker(AbstractSkill skill) {
        this.skill = skill;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }
}
