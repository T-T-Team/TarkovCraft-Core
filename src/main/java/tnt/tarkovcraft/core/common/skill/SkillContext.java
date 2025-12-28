package tnt.tarkovcraft.core.common.skill;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;

public record SkillContext(SkillTriggerEvent event, SkillDefinition definition, Skill skill, float multiplier, Entity entity) {

    @Nullable
    public LivingEntity asLivingEntity() {
        return entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    public Level level() {
        return this.entity.level();
    }
}
