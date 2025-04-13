package tnt.tarkovcraft.core.common.skill;

import net.minecraft.util.context.ContextKey;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;

public final class SkillContextKeys {

    public static final ContextKey<SkillTriggerEvent> EVENT = new ContextKey<>(TarkovCraftCore.createResourceLocation("skill_event"));
    public static final ContextKey<SkillDefinition> DEFINITION = new ContextKey<>(TarkovCraftCore.createResourceLocation("skill_definition"));
    public static final ContextKey<Skill> SKILL = new ContextKey<>(TarkovCraftCore.createResourceLocation("skill"));
    public static final ContextKey<Float> SKILL_GAIN_MULTIPLIER = new ContextKey<>(TarkovCraftCore.createResourceLocation("skill_gain_multiplier"));
}
