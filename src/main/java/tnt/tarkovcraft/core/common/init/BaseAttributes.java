package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;

public final class BaseAttributes {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(TarkovCraftRegistries.ATTRIBUTE, TarkovCraftCore.MOD_ID);

    public static final Holder<Attribute> SPRINT = REGISTRY.register("sprint", Attribute::trueBool);
    public static final Holder<Attribute> ARM_ENERGY_MAX = REGISTRY.register("arm_energy_max", location -> Attribute.create(location, 100));
    public static final Holder<Attribute> ARM_ENERGY_CONSUMPTION = REGISTRY.register("arm_energy_consumption", location -> Attribute.create(location, 1));
    public static final Holder<Attribute> ARM_ENERGY_RECOVERY = REGISTRY.register("arm_energy_recovery", location -> Attribute.create(location, 0.2));
    public static final Holder<Attribute> LEG_ENERGY_MAX = REGISTRY.register("leg_energy_max", location -> Attribute.create(location, 100));
    public static final Holder<Attribute> LEG_ENERGY_CONSUMPTION = REGISTRY.register("leg_energy_consumption", location -> Attribute.create(location, 1));
    public static final Holder<Attribute> LEG_ENERGY_RECOVERY = REGISTRY.register("leg_energy_recovery", location -> Attribute.create(location, 0.2));
}
