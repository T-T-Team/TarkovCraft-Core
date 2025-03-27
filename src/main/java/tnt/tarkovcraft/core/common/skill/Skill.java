package tnt.tarkovcraft.core.common.skill;

public interface Skill {

    SkillType<?> getType();

    void addExperience(int experience);

    int getSkillLevel();

    int getExperience();

    int getMaxExperience();

    boolean isLevelable();
}
