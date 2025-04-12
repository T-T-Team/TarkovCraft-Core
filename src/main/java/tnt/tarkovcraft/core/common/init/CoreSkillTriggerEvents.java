package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.trigger.SkillTriggerEvent;

import java.util.function.Supplier;

public final class CoreSkillTriggerEvents {

    public static final DeferredRegister<SkillTriggerEvent> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.SKILL_TRIGGER_EVENT, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillTriggerEvent> SPRINT = REGISTRY.register("sprint", SkillTriggerEvent::new);
}
