package tnt.tarkovcraft.core.common.init;

import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.attribute.Attribute;

public final class CoreAttributes {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(CoreRegistries.ATTRIBUTE, TarkovCraftCore.MOD_ID);

    public static final Holder<Attribute> PHYSICAL_LEVELING_MULTIPLIER = REGISTRY.register("physical_leveling_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> PRACTICAL_LEVELING_MULTIPLIER = REGISTRY.register("practical_leveling_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> MENTAL_LEVELING_MULTIPLIER = REGISTRY.register("mental_leveling_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> MEMORY_FORGET_TIME_MULTIPLIER = REGISTRY.register("memory_forget_time_multiplier", location -> Attribute.create(location, 1.0F));
    public static final Holder<Attribute> MEMORY_FORGET_AMOUNT_MULTIPLIER = REGISTRY.register("memory_forget_amount_multiplier", location -> Attribute.create(location, 1.0F));
}
