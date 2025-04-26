package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.tracker.SkillTriggerEvent;

import java.util.function.Supplier;

public final class CoreSkillTriggerEvents {

    public static final DeferredRegister<SkillTriggerEvent> REGISTRY = DeferredRegister.create(CoreRegistries.SKILL_TRIGGER_EVENT, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTriggerEvent> PLAYER_TICK = REGISTRY.register("player_tick", SkillTriggerEvent::new);
    public static final Supplier<SkillTriggerEvent> XP_PICKUP = REGISTRY.register("xp_pickup", SkillTriggerEvent::new);
}
