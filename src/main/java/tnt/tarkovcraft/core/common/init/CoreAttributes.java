package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;
import tnt.tarkovcraft.core.common.energy.AbstractStamina;

public final class CoreAttributes {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(CoreRegistries.ATTRIBUTE, TarkovCraftCore.MOD_ID);

    public static final Holder<Attribute> PHYSICAL_LEVELING_MULTIPLIER = REGISTRY.register("physical_leveling_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> MENTAL_LEVELING_MULTIPLIER = REGISTRY.register("mental_leveling_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> LEG_ENERGY_MAX = REGISTRY.register("leg_energy_max", location -> Attribute.create(location, AbstractStamina.DEFAULT_STAMINA_VALUE));
    public static final Holder<Attribute> LEG_ENERGY_CONSUMPTION_MULTIPLIER = REGISTRY.register("leg_energy_consumption_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> LEG_ENERGY_RECOVERY = REGISTRY.register("leg_energy_recovery", location -> Attribute.create(location, 0.5F));
    public static final Holder<Attribute> LEG_ENERGY_RECOVERY_DELAY_MULTIPLER = REGISTRY.register("leg_energy_recovery_delay_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> ARM_ENERGY_MAX = REGISTRY.register("arm_energy_max", location -> Attribute.create(location, AbstractStamina.DEFAULT_STAMINA_VALUE));
    public static final Holder<Attribute> ARM_ENERGY_CONSUMPTION_MULTIPLIER = REGISTRY.register("arm_energy_consumption_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> ARM_ENERGY_RECOVERY = REGISTRY.register("arm_energy_recovery", location -> Attribute.create(location, 0.5F));
    public static final Holder<Attribute> ARM_ENERGY_RECOVERY_DELAY_MULTIPLER = REGISTRY.register("arm_energy_recovery_delay_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> MEMORY_FORGET_TIME_MULTIPLIER = REGISTRY.register("memory_forget_time_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> MEMORY_FORGET_AMOUNT_MULTIPLIER = REGISTRY.register("memory_forget_amount_multiplier", location -> Attribute.create(location, 1.0F));
}
