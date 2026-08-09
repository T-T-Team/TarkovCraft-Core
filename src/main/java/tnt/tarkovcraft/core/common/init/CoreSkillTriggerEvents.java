package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTrigger;

import java.util.function.Supplier;

public final class CoreSkillTriggerEvents {

    public static final DeferredRegister<SkillTrigger> REGISTRY = DeferredRegister.create(CoreRegistries.Keys.SKILL_TRIGGER_EVENT, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTrigger> PLAYER_TICK = REGISTRY.register("player_tick", SkillTrigger::new);
    public static final Supplier<SkillTrigger> XP_PICKUP = REGISTRY.register("xp_pickup", SkillTrigger::new);
}
