package tnt.tarkovcraft.core.common.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.skill.stat.AddAttributeModifierStat;
import tnt.tarkovcraft.core.common.skill.stat.SkillStatType;

import java.util.function.Supplier;

public final class CoreSkillStats {

    public static final DeferredRegister<SkillStatType<?>> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.SKILL_STAT, TarkovCraftCore.MOD_ID);

    public static final Supplier<SkillStatType<AddAttributeModifierStat>> ADD_MODIFIER = REGISTRY.register("add_attribute_modifier", key -> new SkillStatType<>(key, AddAttributeModifierStat.CODEC));
}
